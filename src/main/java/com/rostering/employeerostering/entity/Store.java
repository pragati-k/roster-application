package com.rostering.employeerostering.entity;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalTime;
import java.util.List;

@Getter
@Setter
public class Store {
    private int storeId;
    private String name;
    private Integer parentStoreId;
    private List<LocalTime> storeHours;
    private List<WorkerRequirement> workerRequirement;


}
