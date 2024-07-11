package com.rostering.employeerostering.repository;

import com.rostering.employeerostering.entity.ShiftPattern;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ShiftPatternRepo extends JpaRepository<ShiftPattern, Integer> {

    List<ShiftPattern> findAll();
}
