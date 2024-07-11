package com.rostering.employeerostering.service;

import com.rostering.employeerostering.entity.ScheduleModel;
import com.rostering.employeerostering.repository.ScheduleModelRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ScheduleModelService {

    @Autowired
    private ScheduleModelRepo scheduleModelRepo;

    public List<ScheduleModel> getAllScheduleModel(){
     return scheduleModelRepo.findAll();
    }
}
