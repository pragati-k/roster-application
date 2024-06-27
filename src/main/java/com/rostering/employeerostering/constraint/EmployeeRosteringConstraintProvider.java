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
                constraintFactory.forEachIncludingNullVars(ShiftAssignment.class)
                        .filter(shiftAssignment -> shiftAssignment.getEmployee() == null)
                        .penalize("Each shift must have an employee assigned", HardSoftScore.ONE_HARD),
                employeeConflict(constraintFactory),
                sameShiftForWeek(constraintFactory),
                employeeAvailability(constraintFactory),
                employeeSkillMatch(constraintFactory),
                employeeStoreAssignmentConstraint(constraintFactory),

//                shiftEmployeeCountConstraint(constraintFactory),
//                oneShiftPerDay(constraintFactory),

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
    private boolean isAvailable(ShiftAssignment assignment, Employee employee) {
        int day = DayOfWeek.valueOf(assignment.getDateShift().getDay().toUpperCase()).getValue();
        return employee.getScheduleModel().get(0).getDurationPattern().getShiftType().get(day-1).stream().anyMatch(availability ->
                !availability.getTimings().get(0).getStart().isAfter(assignment.getDateShift().getStartTime()) &&
                        !availability.getTimings().get(0).getEnd().isBefore(assignment.getDateShift().getEndTime()));
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

//    private  Constraint oneShiftPerDay(ConstraintFactory constraintFactory) {
//
//        return constraintFactory.forEach(ShiftAssignment.class)
//                .filter(shiftAssignment -> !shiftAssignment.getEmployee().isEmpty())
//                .join(ShiftAssignment.class,
//                        Joiners.equal(ShiftAssignment::getEmployee),
//                        Joiners.equal(assignment -> assignment.getDateShift().getDate()),
//                        Joiners.lessThan(ShiftAssignment::getId)) // To avoid duplicate pairings
//                .penalize("One shift per day per employee", HardSoftScore.ONE_HARD);
//    }

    private Constraint sameShiftForWeek(ConstraintFactory constraintFactory) {
        return constraintFactory.forEach(ShiftAssignment.class)
                .join(ShiftAssignment.class,
                        Joiners.equal(ShiftAssignment::getEmployee),
                        Joiners.lessThan(ShiftAssignment::getId)) // To avoid duplicate pairings
                .filter((assignment1, assignment2) -> {
                    // Check if the shifts are at the same time but on different days
                    DateShift shift1 = assignment1.getDateShift();
                    DateShift shift2 = assignment2.getDateShift();
                    return !shift1.getDate().equals(shift2.getDate()) &&
                            shift1.getStartTime().equals(shift2.getStartTime()) &&
                            shift1.getEndTime().equals(shift2.getEndTime());
                })
                .reward("Same shift for the week", HardSoftScore.ONE_SOFT);
    }
}
