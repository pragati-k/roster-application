package com.rostering.employeerostering.constraint;

import com.rostering.employeerostering.dto.EmployeeDTO;
import com.rostering.employeerostering.dto.EmployeePairDTO;
import com.rostering.employeerostering.entity.*;
import com.rostering.employeerostering.service.ScheduleModelRotation;
import com.rostering.employeerostering.service.ShiftPatternService;
import org.optaplanner.core.api.score.buildin.hardsoft.HardSoftScore;
import org.optaplanner.core.api.score.stream.*;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.time.temporal.WeekFields;
import java.util.List;
import java.util.stream.Collectors;
import java.util.Locale;

public class EmployeeRosteringConstraintProvider implements ConstraintProvider {

    List<EmployeePairDTO> employeePairList = ScheduleModelRotation.getEmployeePair();


    public EmployeeRosteringConstraintProvider() throws IOException {
    }


    @Override
    public Constraint[] defineConstraints(ConstraintFactory constraintFactory) {
        return new Constraint[]{
                constraintFactory.forEachIncludingNullVars(ShiftAssignment.class)
                        .filter(shiftAssignment -> shiftAssignment.getEmployee() == null)
                        .penalize("Each shift must have an employee assigned", HardSoftScore.ONE_HARD),
                employeeConflict(constraintFactory),
                sameShiftForWeek(constraintFactory),
                employeeAvailability(constraintFactory),
                employeeSkillMatch(constraintFactory),
                employeeStoreAssignmentConstraint(constraintFactory),
                noRepeatShiftTimingsForConsecutiveWeeks(constraintFactory),
////                ensurePairsScheduledTogether(constraintFactory),
                oneShiftPerDay(constraintFactory),
//                assignedOnHoliday(constraintFactory),

//                shiftEmployeeCountConstraint(constraintFactory),

        };
    }


    private Constraint employeeConflict(ConstraintFactory constraintFactory) {
        return constraintFactory.forEach(ShiftAssignment.class)
                .join(ShiftAssignment.class,
                        Joiners.equal(ShiftAssignment::getEmployee),
                        Joiners.lessThan(ShiftAssignment::getId)) // Avoid duplicate pairings and self-joins
                .filter((assignment1, assignment2) -> overlaps(assignment1.getDateShift(), assignment2.getDateShift()))
                .penalize("Overlapping shifts", HardSoftScore.ONE_HARD);
    }

    private boolean overlaps(DateShift shift1, DateShift shift2) {
        return (shift1.getDate().equals(shift2.getDate())) && (shift1.getStartTime().isBefore(shift2.getEndTime()) &&
                shift2.getStartTime().isBefore(shift1.getEndTime()));
    }

    private Constraint employeeSkillMatch(ConstraintFactory constraintFactory) {
        return constraintFactory.forEach(ShiftAssignment.class)
                .filter(assignment -> !assignment.getEmployee().getPosition().contains(assignment.getDateShift().getType()))
                .penalize("Employee skill mismatch", HardSoftScore.ONE_HARD);
    }

    private Constraint employeeAvailability(ConstraintFactory constraintFactory) {
        return constraintFactory.forEach(ShiftAssignment.class)
                .filter(assignment -> !isAvailable(assignment, assignment.getEmployee()))
                .penalize("Employee not available", HardSoftScore.ONE_HARD);
    }

//     Helper method to check if an employee is available for the shift
    private boolean isAvailable(ShiftAssignment assignment, EmployeeDTO employee) {
        int day = DayOfWeek.valueOf(assignment.getDateShift().getDay().toUpperCase()).getValue();
        int index = getWeeknumber(assignment.getStartDate(), assignment.getDateShift().getDate());
        ShiftPattern shiftPattern = employee.getScheduleModel().getShiftPatternList().stream().filter(shiftPattern1 -> shiftPattern1.getId() == employee.getScheduleModelIds().get(index)).collect(Collectors.toList()).get(0);
        return shiftPattern.getShiftTimings().stream().anyMatch(availability ->
                availability.getDay() == day &&
                !availability.getStart().isAfter(assignment.getDateShift().getStartTime()) &&
                        !availability.getEnd().isBefore(assignment.getDateShift().getEndTime()));
    }

    private int getWeeknumber(LocalDate startDate, LocalDate currentDate){

        WeekFields weekFields = WeekFields.of(Locale.getDefault());
        int startWeek = startDate.get(weekFields.weekOfWeekBasedYear());
        int endWeek = currentDate.get(weekFields.weekOfWeekBasedYear());
        int startYear = startDate.getYear();
        int endYear = currentDate.getYear();

        return (endYear - startYear) * 52 + (endWeek - startWeek);
    }

    private Constraint employeeStoreAssignmentConstraint(ConstraintFactory constraintFactory) {
        return constraintFactory.forEach(ShiftAssignment.class)
                .filter(shiftAssignment -> shiftAssignment.getEmployee().getAssignedStoreId() != shiftAssignment.getStore_id())
                .penalize("Employee store assignment conflict", HardSoftScore.ONE_HARD);
    }

//    private Constraint shiftEmployeeCountConstraint(ConstraintFactory constraintFactory) {
//        return constraintFactory
//                .forEach(ShiftAssignment.class)
//                .filter(shiftAssignment -> shiftAssignment.getEmployee().size() != shiftAssignment.getDateShift().getRequired())
//                .penalize("Incorrect employee count", HardSoftScore.ONE_HARD,
//                        shiftAssignment -> Math.abs(shiftAssignment.getDateShift().getRequired() - shiftAssignment.getEmployee().size()));
//    }

    private  Constraint oneShiftPerDay(ConstraintFactory constraintFactory) {

        return constraintFactory.forEach(ShiftAssignment.class)
                .join(ShiftAssignment.class,
                        Joiners.equal(ShiftAssignment::getEmployee),
                        Joiners.equal(assignment -> assignment.getDateShift().getDate()),
                        Joiners.lessThan(ShiftAssignment::getId)) // To avoid duplicate pairings
                .penalize("One shift per day per employee", HardSoftScore.ONE_HARD);
    }

    private Constraint sameShiftForWeek(ConstraintFactory constraintFactory) {
        return constraintFactory.forEach(ShiftAssignment.class)
                .join(ShiftAssignment.class,
                        Joiners.equal(ShiftAssignment::getEmployee),
                        Joiners.lessThan(ShiftAssignment::getId)) // To avoid duplicate pairings
                .filter((assignment1, assignment2) -> {
                    // Check if the shifts are at the same time but on different days
                    DateShift shift1 = assignment1.getDateShift();
                    DateShift shift2 = assignment2.getDateShift();

                    // Determine if shifts are in the same week
                    boolean sameWeek = isInSameWeek(shift1.getDate(), shift2.getDate());

                    // Check if shifts have the same start and end time
                    boolean sameTime = shift1.getStartTime().equals(shift2.getStartTime()) &&
                            shift1.getEndTime().equals(shift2.getEndTime());
                    // Ensure shifts are not on the same date
                    boolean differentDates = !shift1.getDate().isEqual(shift2.getDate());

                    boolean availableForShift = isAvailable(assignment1, assignment1.getEmployee()) && isAvailable(assignment2, assignment2.getEmployee());

                    return sameWeek && sameTime && differentDates && availableForShift;
                })
                .reward("Same shift for the week", HardSoftScore.ONE_SOFT);
    }

    // Helper method to check if two dates are in the same ISO week
    private boolean isInSameWeek(LocalDate date1, LocalDate date2) {
        return date1.get(WeekFields.ISO.weekOfWeekBasedYear()) == date2.get(WeekFields.ISO.weekOfWeekBasedYear()) &&
                date1.getYear() == date2.getYear();
    }

    public Constraint noRepeatShiftTimingsForConsecutiveWeeks(ConstraintFactory constraintFactory) {
        return constraintFactory.forEach(ShiftAssignment.class)
                .join(ShiftAssignment.class,
                        Joiners.equal(ShiftAssignment::getEmployee),
                        Joiners.lessThan(ShiftAssignment::getId)) // To avoid duplicate pairings
                .filter((assignment1, assignment2) -> {
                    // Check if the shifts are within consecutive weeks
                    DateShift shift1 = assignment1.getDateShift();
                    DateShift shift2 = assignment2.getDateShift();

                    // Determine if shifts are within consecutive weeks
                    boolean consecutiveWeeks = areInConsecutiveWeeks(shift1.getDate(), shift2.getDate());

                    boolean sameDay = assignment1.getDateShift().getDay().equals(assignment2.getDateShift().getDay());

                    // Check if shifts have the same timing type (morning/afternoon)
                    boolean sameTimingType = assignment1.getShiftTimingType().equals(assignment2.getShiftTimingType());

                    return consecutiveWeeks && sameTimingType && sameDay;
                })
                .penalize("Repeat shift timings within consecutive weeks", HardSoftScore.ONE_HARD);
    }

    // Helper method to check if two dates are in consecutive ISO weeks
    private boolean areInConsecutiveWeeks(LocalDate date1, LocalDate date2) {
        int weekOfYear1 = date1.get(WeekFields.ISO.weekOfWeekBasedYear());
        int weekOfYear2 = date2.get(WeekFields.ISO.weekOfWeekBasedYear());
        int year1 = date1.getYear();
        int year2 = date2.getYear();

        // Check if dates are in consecutive ISO weeks
        if (year1 == year2 && Math.abs(weekOfYear1 - weekOfYear2) == 1) {
            return true;
        } else if (Math.abs(year1 - year2) == 1) {
            // Check for year boundary cases
            if (year1 < year2 && weekOfYear1 == 52 && weekOfYear2 == 1) {
                return true;
            } else if (year1 > year2 && weekOfYear1 == 1 && weekOfYear2 == 52) {
                return true;
            }
        }
        return false;
    }

    public Constraint ensurePairsScheduledTogether(ConstraintFactory constraintFactory) {
        return constraintFactory.forEach(ShiftAssignment.class)
                .join(ShiftAssignment.class,
                        Joiners.lessThan(ShiftAssignment::getId)) // To avoid duplicate pairings
                .filter((assignment1, assignment2) -> {
                    EmployeeDTO employee1 = assignment1.getEmployee();
                    EmployeeDTO employee2 = assignment2.getEmployee();

                    // Check if the pair is in the requiredPairs list
                    boolean isRequiredPair = employeePairList.stream().anyMatch(pair ->
                            (pair.getWorkerId1().equals(employee1.getWorkerId()) && pair.getWorkerId2().equals(employee2.getWorkerId())) ||
                                    (pair.getWorkerId1().equals(employee2.getWorkerId()) && pair.getWorkerId2().equals(employee1.getWorkerId()))
                    );

                    if (!isRequiredPair) {
                        return false;
                    }

                    // Check if both assignments are on the same day and have the same shift timing
                    DateShift shift1 = assignment1.getDateShift();
                    DateShift shift2 = assignment2.getDateShift();

                    return shift1.getDate().equals(shift2.getDate()) &&
                            assignment1.getShiftTimingType().equals(assignment2.getShiftTimingType());
                })
                .reward("Ensure pairs scheduled together in same shift timing", HardSoftScore.ONE_SOFT);
    }

    public Constraint assignedOnHoliday(ConstraintFactory constraintFactory){
        return constraintFactory.forEach(ShiftAssignment.class)
                .filter(shiftAssignment -> onHoliday(shiftAssignment.getEmployee(), shiftAssignment.getDateShift().getDate()))
                .penalize("Employee On Holiday", HardSoftScore.ONE_HARD);
    }

    private boolean onHoliday(EmployeeDTO employee, LocalDate date){
        boolean isOnWinterHoliday = false;
        boolean isOnSummerHoliday = false;
        if(employee.getWinterVacationStart() != null && employee.getWinterVacationEnd() != null){
           isOnWinterHoliday =  (date.isEqual(employee.getWinterVacationStart()) || date.isAfter(employee.getWinterVacationStart())) && (date.isEqual(employee.getWinterVacationEnd()) || date.isBefore(employee.getWinterVacationEnd()));
        }
        if(employee.getSummerVacationStart() != null && employee.getSummerVacationEnd() != null){
            isOnSummerHoliday = (date.isEqual(employee.getSummerVacationStart()) || date.isAfter(employee.getSummerVacationStart())) && (date.isEqual(employee.getSummerVacationEnd()) || date.isBefore(employee.getSummerVacationEnd()));
        }
        return isOnSummerHoliday || isOnWinterHoliday;
    }
}
