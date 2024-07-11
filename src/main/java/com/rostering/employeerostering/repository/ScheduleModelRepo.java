package com.rostering.employeerostering.repository;

import com.rostering.employeerostering.entity.ScheduleModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ScheduleModelRepo extends JpaRepository<ScheduleModel, Integer> {

    @Override
    List<ScheduleModel> findAll();
}
