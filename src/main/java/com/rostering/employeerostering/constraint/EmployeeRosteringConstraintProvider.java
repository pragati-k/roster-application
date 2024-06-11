package com.rostering.employeerostering.constraint;

import com.rostering.employeerostering.entity.Shift;
import com.rostering.employeerostering.entity.ShiftAssignment;
import org.optaplanner.core.api.score.buildin.hardsoft.HardSoftScore;
import org.optaplanner.core.api.score.stream.*;

public class EmployeeRosteringConstraintProvider implements ConstraintProvider {


    @Override
    public Constraint[] defineConstraints(ConstraintFactory constraintFactory) {
        return new Constraint[]{
                constraintFactory.forEach(ShiftAssignment.class)
                        .groupBy(ShiftAssignment::getShift, ConstraintCollectors.count())
                        .filter((shift, count) -> count != 1)
                        .penalize("Each shift must be assigned to one employee", HardSoftScore.ONE_HARD),
                constraintFactory.forEach(ShiftAssignment.class)
                        .filter(shiftAssignment -> shiftAssignment.getEmployee() == null)
                        .penalize("Each shift must have an employee assigned", HardSoftScore.ONE_HARD),
                employeeConflict(constraintFactory),
                employeeSkillMatch(constraintFactory),
                employeeAvailability(constraintFactory),
                employeeStoreAssignmentConstraint(constraintFactory),
//                constraintFactory.forEach(ShiftAssignment.class)
//                        .filter(shiftAssignment ->
//                                shiftAssignment.getEmployee().getAvailabilities().stream()
//                                        .anyMatch(preferredShift ->
//                                                preferredShift.getStartTime().isBefore(shiftAssignment.getShift().getEndTime()) &&
//                                                        preferredShift.getEndTime().isAfter(shiftAssignment.getShift().getStartTime())))
//                        .reward("Preferred shifts", HardSoftScore.ONE_SOFT)
        };
    }

    private Constraint employeeConflict(ConstraintFactory constraintFactory) {
        return constraintFactory.forEach(ShiftAssignment.class)
                .join(ShiftAssignment.class,
                        Joiners.equal(ShiftAssignment::getEmployee))
                .filter((assignment1, assignment2) ->
                        !assignment1.equals(assignment2) &&
                                overlaps(assignment1.getShift(), assignment2.getShift())
                )
                .penalize("Employee conflict - overlapping shifts", HardSoftScore.ONE_HARD);
    }

    private boolean overlaps(Shift shift1, Shift shift2) {
        return shift1.getStartTime().isBefore(shift2.getEndTime()) &&
                shift2.getStartTime().isBefore(shift1.getEndTime());
    }

    private Constraint employeeSkillMatch(ConstraintFactory constraintFactory) {
        return constraintFactory.forEach(ShiftAssignment.class)
                .filter(assignment -> !assignment.getEmployee().getSkills().containsAll(assignment.getShift().getSkills()))
                .penalize("Employee skill mismatch", HardSoftScore.ONE_HARD);
    }

    private Constraint employeeAvailability(ConstraintFactory constraintFactory) {
        return constraintFactory.forEach(ShiftAssignment.class)
                .filter(assignment -> !isAvailable(assignment))
                .penalize("Employee not available", HardSoftScore.ONE_HARD);
    }

    // Helper method to check if an employee is available for the shift
    private boolean isAvailable(ShiftAssignment assignment) {
        return assignment.getEmployee().getAvailabilities().stream().anyMatch(availability ->
                availability.getDays().containsAll(assignment.getShift().getDays()) &&
                        !availability.getStartTime().isAfter(assignment.getShift().getStartTime()) &&
                        !availability.getEndTime().isBefore(assignment.getShift().getEndTime())
        );
    }

    private Constraint employeeStoreAssignmentConstraint(ConstraintFactory constraintFactory) {
        return constraintFactory.forEach(ShiftAssignment.class)
                .filter(shiftAssignment -> !shiftAssignment.getEmployee().getStore_name().equals(shiftAssignment.getStore_name()))
                .penalize("Employee store assignment conflict", HardSoftScore.ONE_HARD);
    }
}
