package com.rostering.employeerostering.entity;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class DurationPattern {

    private String days;
    private List<List<ShiftType>> shiftType;

    @Override
    public String toString() {
        return "DurationPattern{" +
                "days='" + days + '\'' +
                ", shiftType=" + shiftType +
                '}';
    }
}
