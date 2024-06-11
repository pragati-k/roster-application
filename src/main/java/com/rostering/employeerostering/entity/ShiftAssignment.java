package com.rostering.employeerostering.entity;

import jakarta.persistence.*;
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
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @PlanningId
    private Integer id;

    @ManyToOne
    private Shift shift;

    private String store_name;
    private String shift_type;

    @PlanningListVariable(valueRangeProviderRefs = "employeeRange")
    private List<Employee> employee = new ArrayList<>();

    @Override
    public String toString() {
        return "ShiftAssignment{" +
                "id=" + id +
                ", shift=" + shift +
                ", store_name='" + store_name + '\'' +
                ", employee=" + employee +
                '}';
    }
}
