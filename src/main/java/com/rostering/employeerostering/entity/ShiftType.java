package com.rostering.employeerostering.entity;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class ShiftType {

    private int id;
    private String name;
    private String abbreviation;
    private List<ShiftTiming> timings;
}
