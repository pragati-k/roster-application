package com.rostering.employeerostering.controller;

import com.rostering.employeerostering.entity.Roster;
import lombok.Getter;
import lombok.Setter;
import org.optaplanner.core.api.score.buildin.hardsoft.HardSoftScore;

import org.optaplanner.core.api.solver.SolverJob;
import org.optaplanner.core.api.solver.SolverManager;
import org.optaplanner.core.api.solver.SolverStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api/roster")
public class RosteringController {

    @Autowired
    private SolverManager<Roster, UUID> solverManager;
    private final ConcurrentMap<UUID, SolverJob<Roster, UUID>> solverJobMap = new ConcurrentHashMap<>();

    private final ConcurrentMap<UUID, Roster> bestSolutionMap = new ConcurrentHashMap<>();


    @PostMapping("/solve")
    public UUID solveRoster(@RequestBody Roster roster) {
        UUID problemId = UUID.randomUUID();
        SolverJob<Roster, UUID> solverJob = solverManager.solveAndListen(problemId,
                id -> roster,
                bestSolution -> {
            System.out.println(LocalDateTime.now());
            bestSolution.getShiftAssignmentList().forEach(shiftAssignment ->
                    System.out.println(shiftAssignment.getShift().getId() + " "+ shiftAssignment.getEmployee().getId()));
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
            response.put("message","Solver job not found for problemId: " + problemId);
            return  ResponseEntity.ok(response);
        }
    }
    @GetMapping("/solverStatus/{problemId}")
    public SolverStatus getSolverStatus(@PathVariable UUID problemId) {
        return solverManager.getSolverStatus(problemId);
    }

}