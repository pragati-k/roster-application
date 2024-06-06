package com.rostering.employeerostering.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.optaplanner.core.api.domain.entity.PlanningEntity;
import org.optaplanner.core.api.domain.variable.PlanningVariable;


@Getter
@Setter
@PlanningEntity
public class ShiftAssignment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    private Shift shift;

    @PlanningVariable(valueRangeProviderRefs = "employeeRange")
    private Employee employee;

}
