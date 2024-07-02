package com.rostering.employeerostering.entity;

import com.rostering.employeerostering.dto.EmployeeDTO;

public class EmployeePair {
    private EmployeeDTO employee1;
    private EmployeeDTO employee2;

    // Constructor, getters, and setters

    public EmployeePair(EmployeeDTO employee1, EmployeeDTO employee2) {
        this.employee1 = employee1;
        this.employee2 = employee2;
    }

    public EmployeeDTO getEmployee1() {
        return employee1;
    }

    public EmployeeDTO getEmployee2() {
        return employee2;
    }
}
