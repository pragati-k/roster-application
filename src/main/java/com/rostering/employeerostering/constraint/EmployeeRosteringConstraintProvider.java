package com.rostering.employeerostering.constraint;

import com.rostering.employeerostering.entity.*;
import org.optaplanner.core.api.score.buildin.hardsoft.HardSoftScore;
import org.optaplanner.core.api.score.stream.*;
import java.time.DayOfWeek;
import java.util.Collections;

public class EmployeeRosteringConstraintProvider implements ConstraintProvider {


    @Override
    public Constraint[] defineConstraints(ConstraintFactory constraintFactory) {
        return new Constraint[]{
                constraintFactory.forEach(ShiftAssignment.class)
                        .filter(shiftAssignment -> shiftAssignment.getEmployee() == null || shiftAssignment.getEmployee().isEmpty())
                        .penalize("Each shift must have an employee assigned", HardSoftScore.ofHard(10)),
//                employeeConflict(constraintFactory),
//                employeeSkillMatch(constraintFactory),
                employeeAvailability(constraintFactory),
////              employeeStoreAssignmentConstraint(constraintFactory),
//                shiftEmployeeCountConstraint(constraintFactory)
        };
    }


    private Constraint employeeConflict(ConstraintFactory constraintFactory) {
        return constraintFactory.forEach(ShiftAssignment.class)
                .join(ShiftAssignment.class,
                        Joiners.lessThan(ShiftAssignment::getId)) // Prevent duplicate pairings
                .filter((assignment1, assignment2) -> {
                    for (Employee employee1 : assignment1.getEmployee()) {
                        for (Employee employee2 : assignment2.getEmployee()) {
                            if (employee1.equals(employee2) && overlaps(assignment1.getDateShift(), assignment2.getDateShift())) {
                                return true;
                            }
                        }
                    }
                    return false;
                })
                .penalize("Employee conflict - overlapping shifts", HardSoftScore.ONE_HARD);
    }

    private boolean overlaps(DateShift shift1, DateShift shift2) {
        return (shift1.getDate().equals(shift2.getDate())) && (shift1.getStartTime().isBefore(shift2.getEndTime()) &&
                shift2.getStartTime().isBefore(shift1.getEndTime()));
    }

    private Constraint employeeSkillMatch(ConstraintFactory constraintFactory) {
        return constraintFactory.forEach(ShiftAssignment.class)
                .filter(assignment -> assignment.getEmployee().stream()
                        .anyMatch(employee -> !employee.getPosition().containsAll(Collections.singleton(assignment.getDateShift().getType()))))
                .penalize("Employee skill mismatch", HardSoftScore.ONE_HARD);
    }

    private Constraint employeeAvailability(ConstraintFactory constraintFactory) {
        return constraintFactory.forEach(ShiftAssignment.class)
                .filter(assignment -> assignment.getEmployee().stream()
                        .anyMatch(employee -> !isAvailable(assignment, employee)))
                .penalize("Employee not available", HardSoftScore.ONE_HARD);
    }

//     Helper method to check if an employee is available for the shift
    private boolean isAvailable(ShiftAssignment assignment, Employee employee) {
        int day = DayOfWeek.valueOf(assignment.getDateShift().getDay().toUpperCase()).getValue();
        return employee.getScheduleModel().get(0).getDurationPattern().getShiftType().get(day-1).stream().anyMatch(availability ->
                !availability.getTimings().get(0).getStart().isAfter(assignment.getDateShift().getStartTime()) &&
                        !availability.getTimings().get(0).getEnd().isBefore(assignment.getDateShift().getEndTime()));
    }


//    private Constraint employeeStoreAssignmentConstraint(ConstraintFactory constraintFactory) {
//        return constraintFactory.forEach(ShiftAssignment.class)
//                .filter(shiftAssignment -> shiftAssignment.getEmployee().stream()
//                        .anyMatch(employee -> employee.getAssignedStoreId() != shiftAssignment.getId()))
//                .penalize("Employee store assignment conflict", HardSoftScore.ONE_HARD);
//    }

    private Constraint shiftEmployeeCountConstraint(ConstraintFactory constraintFactory) {
        return constraintFactory
                .forEach(ShiftAssignment.class)
                .filter(shiftAssignment -> shiftAssignment.getEmployee().size() != shiftAssignment.getDateShift().getRequired())
                .penalize("Incorrect employee count", HardSoftScore.ONE_HARD,
                        shiftAssignment -> Math.abs(shiftAssignment.getDateShift().getRequired() - shiftAssignment.getEmployee().size()));
    }
}
