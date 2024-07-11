package com.rostering.employeerostering.service;

import com.rostering.employeerostering.entity.Employee;
import com.rostering.employeerostering.repository.EmployeeRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EmployeeService {

    @Autowired
    private EmployeeRepo employeeRepo;

    public List<Employee> getAllEmployee(){
        return employeeRepo.findAll();
    }
}
