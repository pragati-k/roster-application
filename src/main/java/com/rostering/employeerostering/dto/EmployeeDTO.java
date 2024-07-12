package com.rostering.employeerostering.dto;


import com.rostering.employeerostering.entity.ScheduleModel;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;


@Getter
@Setter
@Builder
public class EmployeeDTO {

    private int workerId;
    private String name;
    private List<String> position;
    private int assignedStoreId;
    private List<Integer> scheduleModelIds;
    private ScheduleModel scheduleModel;
    private String totalWorkingHours;
    private String employmentType;
    private LocalDate winterVacationStart;
    private LocalDate winterVacationEnd;
    private LocalDate summerVacationStart;
    private LocalDate summerVacationEnd;


    @Override
    public String toString() {
        return "Employee{" +
                "id='" + workerId + '\'' +
                ", name='" + name + '\'' +
                ", positions=" + position +
                ", assignedStoreId=" + assignedStoreId +
                ", scheduleModelIds=" + scheduleModelIds +
                '}';
    }
}
