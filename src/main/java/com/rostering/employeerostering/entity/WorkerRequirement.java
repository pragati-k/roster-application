package com.rostering.employeerostering.entity;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class WorkerRequirement {

    private String type;
    private List<Shift> shift;
}
