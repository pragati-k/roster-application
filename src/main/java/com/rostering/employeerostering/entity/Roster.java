package com.rostering.employeerostering.entity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.OneToMany;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.optaplanner.core.api.domain.solution.PlanningEntityCollectionProperty;
import org.optaplanner.core.api.domain.solution.PlanningScore;
import org.optaplanner.core.api.domain.solution.PlanningSolution;
import org.optaplanner.core.api.domain.solution.ProblemFactCollectionProperty;
import org.optaplanner.core.api.domain.valuerange.ValueRangeProvider;
import org.optaplanner.core.api.score.buildin.hardsoft.HardSoftScore;

import java.util.ArrayList;
import java.util.List;

@PlanningSolution
@Getter
@Setter
public class Roster {

    private List<Employee> employeeList;

    private List<RequiredShifts> requiredShifts;

    private List<ShiftAssignment> shiftAssignmentList = new ArrayList<>();

    private List<Store> storeList;

    @PlanningScore
    private HardSoftScore score;

    @ProblemFactCollectionProperty
    public List<Store> getStoreList() {
        return storeList;
    }

    public void setStoreList(List<Store> storeList) {
        this.storeList =  storeList;
    }
    @ProblemFactCollectionProperty
    @ValueRangeProvider(id = "employeeRange")
    public List<Employee> getEmployeeList() {
        return employeeList;
    }

    public void setEmployeeList(List<Employee> employeeList) {
        this.employeeList = employeeList;
    }
    @PlanningEntityCollectionProperty
    public List<ShiftAssignment> getShiftAssignmentList() {
        return shiftAssignmentList;
    }

    public void setShiftAssignmentList(List<ShiftAssignment> shiftAssignmentList) {
        this.shiftAssignmentList = shiftAssignmentList;
    }
}
