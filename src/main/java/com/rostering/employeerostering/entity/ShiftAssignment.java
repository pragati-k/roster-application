package com.rostering.employeerostering.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.optaplanner.core.api.domain.entity.PlanningEntity;
import org.optaplanner.core.api.domain.lookup.PlanningId;
import org.optaplanner.core.api.domain.variable.PlanningVariable;


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

    @PlanningVariable(valueRangeProviderRefs = "employeeRange")
    private Employee employee;

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
