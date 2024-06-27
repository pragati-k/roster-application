package com.rostering.employeerostering.entity;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
public class Seasons {

    private String seasonType;

    private List<LocalDate> seasonDate;

    private List<WorkerRequirement> workerRequirement;
}
