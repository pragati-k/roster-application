package com.rostering.employeerostering.entity;

import lombok.Getter;
import lombok.Setter;
import org.optaplanner.core.api.domain.entity.PlanningEntity;
import org.optaplanner.core.api.domain.lookup.PlanningId;
import org.optaplanner.core.api.domain.variable.PlanningListVariable;
import org.optaplanner.core.api.domain.variable.PlanningVariable;

import java.util.ArrayList;
import java.util.List;


@Getter
@Setter
@PlanningEntity
public class ShiftAssignment {

    @PlanningId
    private Integer id;

    private DateShift dateShift;

    private int store_id;

    @PlanningVariable(valueRangeProviderRefs = "employeeRange")
    private Employee employee ;

    @Override
    public String toString() {
        return "ShiftAssignment{" +
                "id=" + id +
                ", dateShift=" + dateShift +
                ", store_name='" + store_id + '\'' +
                ", employee=" + employee +
                '}';
    }
}
