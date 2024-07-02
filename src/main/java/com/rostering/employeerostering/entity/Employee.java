package com.rostering.employeerostering.entity;


import lombok.*;

import java.util.List;

@Getter
@Setter
public class Employee {

    private String workerId;
    private String name;
    private List<String> position;
    private int assignedStoreId;
    private ContactInfo contactInfo;
    private List<ScheduleModel> scheduleModel;


    public Employee(String workerId, String name, List<String> position, int assignedStoreId, ContactInfo contactInfo, List<ScheduleModel> scheduleModel) {
        this.workerId = workerId;
        this.name = name;
        this.position = position;
        this.assignedStoreId = assignedStoreId;
        this.contactInfo = contactInfo;
        this.scheduleModel = scheduleModel;
    }
    @Override
    public String toString() {
        return "Employee{" +
                "id='" + workerId + '\'' +
                ", name='" + name + '\'' +
                ", positions=" + position +
                ", assignedStoreId=" + assignedStoreId +
                ", scheduleModelIds=" + scheduleModel +
                '}';
    }
}
