package com.rostering.employeerostering.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Entity
@Getter
@Setter
public class WorkerRequirement {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private String position;

    @OneToMany(mappedBy = "workerRequirement", cascade = CascadeType.ALL)
    private List<ShiftDetails> allShifts;

    @ManyToOne
    @JoinColumn(name = "season_id")
    @JsonIgnore
    private Seasons season;
}
