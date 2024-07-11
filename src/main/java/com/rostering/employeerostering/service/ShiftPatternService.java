package com.rostering.employeerostering.service;

import com.rostering.employeerostering.entity.ShiftPattern;
import com.rostering.employeerostering.repository.ShiftPatternRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ShiftPatternService {

    @Autowired
    private ShiftPatternRepo shiftPatternRepo;

    public List<ShiftPattern> getAllShiftPattern(){
        return shiftPatternRepo.findAll();
    }
}
