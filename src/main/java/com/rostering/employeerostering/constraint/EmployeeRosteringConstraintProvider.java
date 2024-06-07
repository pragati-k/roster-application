package com.rostering.employeerostering.constraint;

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
                constraintFactory.forEach(ShiftAssignment.class)
                        .join(ShiftAssignment.class,
                                Joiners.equal(ShiftAssignment::getEmployee))
                        .filter((assignment1, assignment2) -> !assignment1.equals(assignment2))
                        .penalize("Employee conflict", HardSoftScore.ONE_HARD),
                constraintFactory.forEachUniquePair(ShiftAssignment.class,
                                Joiners.equal(ShiftAssignment::getEmployee),
                                Joiners.overlapping(
                                        shiftAssignment -> shiftAssignment.getShift().getStartTime(),
                                        shiftAssignment -> shiftAssignment.getShift().getEndTime()
                                ))
                        .penalize("Avoid overlapping shifts", HardSoftScore.ONE_HARD),
                constraintFactory.forEach(ShiftAssignment.class)
                        .filter(shiftAssignment ->
                                shiftAssignment.getEmployee().getPreferredShifts().stream()
                                        .anyMatch(preferredShift ->
                                                preferredShift.getStartTime().isBefore(shiftAssignment.getShift().getEndTime()) &&
                                                        preferredShift.getEndTime().isAfter(shiftAssignment.getShift().getStartTime())))
                        .reward("Preferred shifts", HardSoftScore.ONE_SOFT)
        };
    }
}
