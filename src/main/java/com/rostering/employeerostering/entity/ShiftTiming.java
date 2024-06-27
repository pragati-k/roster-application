package com.rostering.employeerostering.entity;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalTime;

@Getter
@Setter
public class ShiftTiming {

    private LocalTime start;
    private LocalTime end;
    private String totalLength;
    private String effectiveWork;

    @Override
    public String toString() {
        return "ShiftTiming{" +
                "start=" + start +
                ", end=" + end +
                ", totalLength='" + totalLength + '\'' +
                ", effectiveWork='" + effectiveWork + '\'' +
                '}';
    }
}
