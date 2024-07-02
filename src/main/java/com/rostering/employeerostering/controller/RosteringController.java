package com.rostering.employeerostering.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.rostering.employeerostering.dto.ConstraintMatchDTO;
import com.rostering.employeerostering.dto.ConstraintMatchTotalDTO;
import com.rostering.employeerostering.dto.EmployeeDTO;
import com.rostering.employeerostering.dto.ScoreExplanationDTO;
import com.rostering.employeerostering.entity.*;
import com.rostering.employeerostering.service.ScheduleModelRotation;
import com.rostering.employeerostering.service.ShiftGenerator;
import org.optaplanner.core.api.score.ScoreExplanation;
import org.optaplanner.core.api.score.ScoreManager;
import org.optaplanner.core.api.score.buildin.hardsoft.HardSoftScore;

import org.optaplanner.core.api.score.constraint.ConstraintMatch;
import org.optaplanner.core.api.score.constraint.ConstraintMatchTotal;
import org.optaplanner.core.api.solver.SolverJob;
import org.optaplanner.core.api.solver.SolverManager;
import org.optaplanner.core.api.solver.SolverStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api/roster")
public class RosteringController {

    @Autowired
    private SolverManager<Roster, UUID> solverManager;

    @Autowired
    private ScoreManager<Roster, HardSoftScore> scoreManager;

    @Autowired
    private ShiftGenerator shiftGenerator;

    @Autowired
    private ScheduleModelRotation scheduleModelRotation;
    private final ConcurrentMap<UUID, SolverJob<Roster, UUID>> solverJobMap = new ConcurrentHashMap<>();

    private final ConcurrentMap<UUID, Roster> bestSolutionMap = new ConcurrentHashMap<>();


    @PostMapping("/solve")
    public UUID solveRoster(@RequestParam("employees") MultipartFile employeesFile,
                            @RequestParam("stores") MultipartFile storeFile,
                            @RequestParam("startDate") String startDate,
                            @RequestParam("endDate") String endDate) throws IOException {

        Roster roster = getJsonData(employeesFile, storeFile, startDate, endDate);

        UUID problemId = UUID.randomUUID();
        SolverJob<Roster, UUID> solverJob = solverManager.solveAndListen(problemId,
                id -> roster,
                bestSolution -> {
                    System.out.println(LocalDateTime.now());
                    System.out.println(bestSolution.getScore().getHardScore());
                    System.out.println(bestSolution.getScore().getSoftScore());
                    System.out.println(bestSolution.getScore().getInitScore());
                    System.out.println(bestSolution.getScore().isFeasible());
                    System.out.println(bestSolution.getScore().isSolutionInitialized());
                    System.out.println(bestSolution.getScore().isZero());
                    bestSolutionMap.put(problemId, bestSolution);
        });
        solverJobMap.put(problemId, solverJob);
        return problemId;
    }

    @GetMapping("/solution/{problemId}")
    public Roster getSolution(@PathVariable UUID problemId) {
        Roster solution = bestSolutionMap.get(problemId);
        for (ShiftAssignment shiftAssignment : solution.getShiftAssignmentList()) {
            if (shiftAssignment.getEmployee() != null) {
                System.out.printf("%s - %s - %s - %s - %s\n", shiftAssignment.getDateShift().getDate(), shiftAssignment.getDateShift().getDay(), shiftAssignment.getDateShift().getType(), shiftAssignment.getDateShift().getStartTime(), shiftAssignment.getDateShift().getEndTime());
                System.out.printf("%s - %s\n", shiftAssignment.getEmployee().getName(), shiftAssignment.getEmployee().getScheduleModelIds());
            }
        }
        return bestSolutionMap.get(problemId);
    }

    @GetMapping("/terminate/{problemId}")
    public ResponseEntity<Map<String, String>> terminateSolver(@PathVariable UUID problemId) {
        SolverJob<Roster, UUID> solverJob = solverJobMap.get(problemId);
        Map<String, String> response = new HashMap<>();
        if (solverJob != null) {
            solverJob.terminateEarly();
            response.put("message","Solver terminated early for problemId: " + problemId);
            return  ResponseEntity.ok(response);
        } else {
            response.put("message", "Solver job not found for problemId: " + problemId);
            return ResponseEntity.ok(response);
        }
    }

    @GetMapping("/solverStatus/{problemId}")
    public SolverStatus getSolverStatus(@PathVariable UUID problemId) {
        return solverManager.getSolverStatus(problemId);
    }


    @GetMapping("/score-explanation/{problemId}")
    public ScoreExplanationDTO getScoreExplanation(@PathVariable UUID problemId) {
        Roster bestSolution = bestSolutionMap.get(problemId);
        ScoreExplanation<Roster, HardSoftScore> scoreExplanation = scoreManager.explainScore(bestSolution);
        return convertScoreExplanationToDTO(scoreExplanation);
    }

    private ScoreExplanationDTO convertScoreExplanationToDTO(ScoreExplanation<Roster, HardSoftScore> scoreExplanation) {
        List<ConstraintMatchTotalDTO> constraintMatchTotalDTOs = scoreExplanation.getConstraintMatchTotalMap().values().stream()
                .map(this::convertConstraintMatchTotalToDTO)
                .collect(Collectors.toList());
        return new ScoreExplanationDTO(scoreExplanation.getScore().toString(), constraintMatchTotalDTOs);
    }

    private ConstraintMatchTotalDTO convertConstraintMatchTotalToDTO(ConstraintMatchTotal constraintMatchTotal) {
        List<ConstraintMatchDTO> constraintMatchDTOs = (List<ConstraintMatchDTO>) constraintMatchTotal.getConstraintMatchSet().stream()
                .map(match -> convertConstraintMatchToDTO((ConstraintMatch) match)).collect(Collectors.toList());
        return new ConstraintMatchTotalDTO(constraintMatchTotal.getConstraintName(), constraintMatchTotal.getScore().toString(), constraintMatchDTOs);
    }

    private ConstraintMatchDTO convertConstraintMatchToDTO(ConstraintMatch constraintMatch) {
        return new ConstraintMatchDTO(constraintMatch.getJustificationList().toString(), constraintMatch.getScore().toString());
    }
    
    private Roster getJsonData(MultipartFile employeesFile, MultipartFile storeFile, String startDate, String endDate) throws IOException {
        // Load JSON files
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());

        List<EmployeeDTO> employeesDTO = Arrays.asList(objectMapper.readValue(employeesFile.getInputStream(), EmployeeDTO[].class));
        ClassPathResource scheduleModelsResource = new ClassPathResource("data/ScheduleModels.json");
        List<ScheduleModel> scheduleModels = Arrays.asList(objectMapper.readValue(scheduleModelsResource.getInputStream(), ScheduleModel[].class));

        long numberOfWeeks = ChronoUnit.WEEKS.between(LocalDate.parse(startDate), LocalDate.parse(endDate)) + 1;

        employeesDTO.forEach(item -> item.setScheduleModelIds(scheduleModelRotation.repeatTillNNonConsecutive(item.getScheduleModelIds(), numberOfWeeks)));
        List<Store> stores = Arrays.asList(objectMapper.readValue(storeFile.getInputStream(), Store[].class));

        Roster roster = new Roster();
        roster.setEmployeeList(employeesDTO);
        List<ShiftAssignment> shiftAssignmentList = new ArrayList<>();


        int i = 0;
        for (Store s : stores) {
            Seasons seasonType = s.getSeasons().stream()
                    .filter(seasons -> LocalDate.parse(startDate).isAfter(seasons.getSeasonDate().get(0)) && LocalDate.parse(endDate).isBefore(seasons.getSeasonDate().get(1)))
                    .findFirst().orElse(new Seasons());
            roster.setSeasonDate(seasonType.getSeasonDate());
            for (WorkerRequirement workerRequirement : seasonType.getWorkerRequirement()) {

                List<DateShift> dateShifts = ShiftGenerator.start(workerRequirement, startDate, endDate);
                for (DateShift shift : dateShifts) {
                    int totalRequried = shift.getRequired();
                    for(int requriedNumber = 0; requriedNumber < totalRequried; requriedNumber++){
                        ShiftAssignment shiftAssignment = new ShiftAssignment();
                        shiftAssignment.setId(i);
                        shiftAssignment.setStore_id(s.getStoreId());
                        shift.setRequired(1);
                        shiftAssignment.setDateShift(shift);
                        shiftAssignmentList.add(shiftAssignment);
                        shiftAssignment.setStartDate(LocalDate.parse(startDate));
                        shiftAssignment.setEndDate(LocalDate.parse(endDate));
                        i++;
                    }
                }
            }
        }
        roster.setShiftAssignmentList(shiftAssignmentList);
        roster.setStoreList(stores);
        return roster;
    }
}