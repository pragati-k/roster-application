package com.rostering.employeerostering.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Entity
@Getter
@Setter
public class ScheduleModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private String name;

    @OneToMany(mappedBy = "scheduleModel", cascade = CascadeType.ALL)
    private List<ShiftPattern> shiftPatternList;
}
