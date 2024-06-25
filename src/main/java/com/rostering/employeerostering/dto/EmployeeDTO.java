package com.rostering.employeerostering.dto;


import com.rostering.employeerostering.entity.ContactInfo;
import lombok.Getter;
import lombok.Setter;

import java.util.List;


@Getter
@Setter
public class EmployeeDTO {

    private String workerId;
    private String name;
    private List<String> position;
    private int assignedStoreId;
    private ContactInfo contactInfo;
    private List<Integer> scheduleModelIds;


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
