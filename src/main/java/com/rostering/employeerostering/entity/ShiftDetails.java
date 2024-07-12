package com.rostering.employeerostering.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.rostering.employeerostering.enums.ShiftTypeEnum;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Entity
@Getter
@Setter
public class ShiftDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Enumerated(EnumType.STRING)
    private ShiftTypeEnum shiftType;

    @OneToMany(mappedBy = "shiftDetails", cascade = CascadeType.ALL)
    private List<Shift> shifts;

    @ManyToOne
    @JoinColumn(name = "worker_requirement_id")
    @JsonIgnore
    private WorkerRequirement workerRequirement;
}

