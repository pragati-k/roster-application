package com.rostering.employeerostering.entity;

import lombok.Getter;
import lombok.Setter;
import org.optaplanner.core.api.domain.entity.PlanningEntity;
import org.optaplanner.core.api.domain.lookup.PlanningId;
import org.optaplanner.core.api.domain.variable.PlanningListVariable;

import java.util.ArrayList;
import java.util.List;


@Getter
@Setter
@PlanningEntity
public class ShiftAssignment {

    @PlanningId
    private Integer id;

    private DateShift dateShift;

    private String store_name;

    @PlanningListVariable(valueRangeProviderRefs = "employeeRange")
    private List<Employee> employee = new ArrayList<>();

    @Override
    public String toString() {
        return "ShiftAssignment{" +
                "id=" + id +
                ", dateShift=" + dateShift +
                ", store_name='" + store_name + '\'' +
                ", employee=" + employee +
                '}';
    }
}
