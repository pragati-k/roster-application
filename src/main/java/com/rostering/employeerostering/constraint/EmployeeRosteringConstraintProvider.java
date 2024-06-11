package com.rostering.employeerostering.constraint;

import com.rostering.employeerostering.entity.Employee;
import com.rostering.employeerostering.entity.RequiredShifts;
import com.rostering.employeerostering.entity.Shift;
import com.rostering.employeerostering.entity.ShiftAssignment;
import org.optaplanner.core.api.score.buildin.hardsoft.HardSoftScore;
import org.optaplanner.core.api.score.stream.*;

public class EmployeeRosteringConstraintProvider implements ConstraintProvider {


    @Override
    public Constraint[] defineConstraints(ConstraintFactory constraintFactory) {
        return new Constraint[]{
//                constraintFactory.forEach(ShiftAssignment.class)
//                        .groupBy(ShiftAssignment::getShift, ConstraintCollectors.count())
//                        .filter((shift, count) -> count > shift.getEmployee_required())
//                        .penalize("Shift has been assigned more than required employee", HardSoftScore.ONE_HARD),
                constraintFactory.forEach(ShiftAssignment.class)
                        .filter(shiftAssignment -> shiftAssignment.getEmployee() == null)
                        .penalize("Each shift must have an employee assigned", HardSoftScore.ONE_HARD),
                employeeConflict(constraintFactory),
                employeeSkillMatch(constraintFactory),
                employeeAvailability(constraintFactory),
                employeeStoreAssignmentConstraint(constraintFactory),
                shiftEmployeeCountConstraint(constraintFactory)
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
                        Joiners.lessThan(ShiftAssignment::getId)) // Prevent duplicate pairings
                .filter((assignment1, assignment2) -> {
                    for (Employee employee1 : assignment1.getEmployee()) {
                        for (Employee employee2 : assignment2.getEmployee()) {
                            if (employee1.equals(employee2) && overlaps(assignment1.getShift(), assignment2.getShift())) {
                                return true;
                            }
                        }
                    }
                    return false;
                })
                .penalize("Employee conflict - overlapping shifts", HardSoftScore.ONE_HARD);
    }

    private boolean overlaps(Shift shift1, Shift shift2) {
        return shift1.getStartTime().isBefore(shift2.getEndTime()) &&
                shift2.getStartTime().isBefore(shift1.getEndTime());
    }

    private Constraint employeeSkillMatch(ConstraintFactory constraintFactory) {
        return constraintFactory.forEach(ShiftAssignment.class)
                .filter(assignment -> assignment.getEmployee().stream()
                        .anyMatch(employee -> !employee.getSkills().containsAll(assignment.getShift().getSkills())))
                .penalize("Employee skill mismatch", HardSoftScore.ofHard(5));
    }

    private Constraint employeeAvailability(ConstraintFactory constraintFactory) {
        return constraintFactory.forEach(ShiftAssignment.class)
                .filter(assignment -> assignment.getEmployee().stream()
                        .anyMatch(employee -> !isAvailable(assignment, employee)))
                .penalize("Employee not available", HardSoftScore.ONE_HARD);
    }

    // Helper method to check if an employee is available for the shift
    private boolean isAvailable(ShiftAssignment assignment, Employee employee) {
        return employee.getAvailabilities().stream().anyMatch(availability ->
                availability.getDays().containsAll(assignment.getShift().getDays()) &&
                        !availability.getStartTime().isAfter(assignment.getShift().getStartTime()) &&
                        !availability.getEndTime().isBefore(assignment.getShift().getEndTime()));
    }


    private Constraint employeeStoreAssignmentConstraint(ConstraintFactory constraintFactory) {
        return constraintFactory.forEach(ShiftAssignment.class)
                .filter(shiftAssignment -> shiftAssignment.getEmployee().stream()
                        .anyMatch(employee -> !employee.getStore_name().equals(shiftAssignment.getStore_name())))
                .penalize("Employee store assignment conflict", HardSoftScore.ONE_HARD);
    }

    private Constraint shiftEmployeeCountConstraint(ConstraintFactory constraintFactory) {
        return constraintFactory
                .forEach(ShiftAssignment.class)
                .filter(shiftAssignment -> shiftAssignment.getEmployee().size() != shiftAssignment.getShift().getEmployee_required())
                .penalize("Incorrect employee count", HardSoftScore.ONE_HARD,
                        shiftAssignment -> Math.abs(shiftAssignment.getShift().getEmployee_required() - shiftAssignment.getEmployee().size()));
    }
}
