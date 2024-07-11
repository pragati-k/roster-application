package com.rostering.employeerostering.entity;


import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Getter
@Setter
public class Employee {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private String name;
    private int storeId;
    private String totalWorkingHours;
    private String employmentType;
    private String position;
    private LocalDate winterVacationStart;
    private LocalDate winterVacationEnd;
    private LocalDate summerVacationStart;
    private LocalDate summerVacationEnd;
    private int startingShiftPatternId;         // for rotating shift

    @ManyToOne
    @JoinColumn(name = "schedule_model_id")
    private ScheduleModel scheduleModel;

}
