package com.rostering.employeerostering.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.rostering.employeerostering.dto.EmployeeDTO;
import com.rostering.employeerostering.entity.*;
import com.rostering.employeerostering.repository.EmployeeRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDate;
import java.time.temporal.WeekFields;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

@Service
public class ShiftGenerator {

    @Autowired
    private ScheduleModelRotation scheduleModelRotation;

    @Autowired
    private ShiftPatternService shiftPatternService;

    @Autowired
    private EmployeeRepo employeeRepo;

    private Roster finalRoster = new Roster();

    public Roster getJsonData(MultipartFile storeFile, String startDate, String endDate) throws IOException {
        // Load JSON files
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());

        long numberOfWeeks = getWeeknumber(LocalDate.parse(startDate), LocalDate.parse(endDate)) + 1;

        List<Employee> employeeList = employeeRepo.findAll();
        List<EmployeeDTO> employeesDTO = new ArrayList<>();
        for(Employee employee: employeeList){
            EmployeeDTO newEmployee = EmployeeDTO.builder()
                    .workerId(employee.getId())
                    .name(employee.getName())
                    .position(Arrays.stream(employee.getPosition().split("/")).toList())
                    .assignedStoreId(employee.getStoreId())
                    .scheduleModel(employee.getScheduleModel())
                    .employmentType(employee.getEmploymentType())
                    .summerVacationStart(employee.getSummerVacationStart())
                    .summerVacationEnd(employee.getSummerVacationEnd())
                    .winterVacationStart(employee.getWinterVacationStart())
                    .winterVacationEnd(employee.getWinterVacationEnd())
                    .totalWorkingHours(employee.getTotalWorkingHours()).build();
            List<Integer> tempScheduleModelId = employee.getScheduleModel().getShiftPatternList().stream().map(item -> item.getId()).collect(Collectors.toList());
            if(employee.getStartingShiftPatternId() != 0){
                tempScheduleModelId = tempScheduleModelId.stream().filter(item -> item != employee.getStartingShiftPatternId()).collect(Collectors.toList());
                tempScheduleModelId.add(0, employee.getStartingShiftPatternId());
            }
            newEmployee.setScheduleModelIds(tempScheduleModelId);
            employeesDTO.add(newEmployee);
        }
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
        finalRoster = roster;
        return roster;
    }

    private int getWeeknumber(LocalDate startDate, LocalDate currentDate){

        WeekFields weekFields = WeekFields.of(Locale.getDefault());
        int startWeek = startDate.get(weekFields.weekOfWeekBasedYear());
        int endWeek = currentDate.get(weekFields.weekOfWeekBasedYear());
        int startYear = startDate.getYear();
        int endYear = currentDate.getYear();

        return (endYear - startYear) * 52 + (endWeek - startWeek);
    }

    public Roster getRoster(){
        return finalRoster;
    }

    public static List<DateShift> generateShifts(LocalDate startDate, LocalDate endDate, WorkerRequirement workerRequirement) {
        List<DateShift> shifts = new ArrayList<>();

        // Iterate over each date in the range
        for (LocalDate date = startDate; !date.isAfter(endDate); date = date.plusDays(1)) {
            String dayOfWeek = date.getDayOfWeek().name().toUpperCase();
                for(Shift shift: workerRequirement.getShift()){
                    if(shift.getDays().contains(dayOfWeek)){
                        DateShift dateShift = new DateShift();
                        dateShift.setType(workerRequirement.getType());
                        dateShift.setDate(date);
                        dateShift.setId(shift.getId());
                        dateShift.setRequired(shift.getRequired());
                        dateShift.setDay(dayOfWeek);
                        dateShift.setStartTime(shift.getStartTime());
                        dateShift.setEndTime(shift.getEndTime());
                        shifts.add(dateShift);
                    }
                }

            }

        return shifts;
    }

    public static List<DateShift> start(WorkerRequirement workerRequirement, String startDate, String endDate) {
        LocalDate start = LocalDate.parse(startDate);
        LocalDate end = LocalDate.parse(endDate);

        List<DateShift> shifts = generateShifts(start, end, workerRequirement);
        return shifts;
    }
}
