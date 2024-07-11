package com.rostering.employeerostering.controller;

import com.rostering.employeerostering.entity.Employee;
import com.rostering.employeerostering.service.EmployeeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/employee")
public class EmployeeController {


    @Autowired
    private EmployeeService employeeService;

    @GetMapping(path = "/allEmployee")
    public ResponseEntity<List<Employee>> getAllEmployee(){

        List<Employee> employeeList = employeeService.getAllEmployee();

        return ResponseEntity.ok(employeeList);
    }
}
