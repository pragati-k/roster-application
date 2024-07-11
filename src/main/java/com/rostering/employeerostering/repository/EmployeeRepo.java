package com.rostering.employeerostering.repository;

import com.rostering.employeerostering.entity.Employee;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EmployeeRepo extends JpaRepository<Employee, Integer> {

    @Query("select emp from Employee emp")
    List<Employee> findAll();
}
