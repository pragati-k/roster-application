package com.rostering.employeerostering.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.List;


@Entity
@Getter
@Setter
public class ShiftPattern {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @ManyToOne
    @JoinColumn(name = "schedule_model_id")
    @JsonIgnore
    private ScheduleModel scheduleModel;

    @OneToMany(mappedBy = "shiftPattern", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private List<ShiftTiming> shiftTimings;
}
