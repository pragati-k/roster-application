package com.rostering.employeerostering.entity;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ScheduleModel {

    private int shiftPatternId;
    private String name;
    private boolean applyShiftOnHoliday;
    private String applyPatternFrom;
    private DurationPattern durationPattern;

    @Override
    public String toString() {
        return "ScheduleModel{" +
                "shiftPatternId=" + shiftPatternId +
                ", name='" + name + '\'' +
                ", applyShiftOnHoliday=" + applyShiftOnHoliday +
                ", applyPatternFrom='" + applyPatternFrom + '\'' +
                ", durationPattern=" + durationPattern +
                '}';
    }
}
