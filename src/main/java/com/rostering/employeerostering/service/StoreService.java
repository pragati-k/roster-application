package com.rostering.employeerostering.service;

import com.rostering.employeerostering.entity.*;
import com.rostering.employeerostering.repository.StoreRepo;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class StoreService {

    @Autowired
    private StoreRepo storeRepo;

    @Transactional
    public void saveAll(List<Store> stores) {
        for (Store store : stores) {
            for (Seasons season : store.getSeasons()) {
                season.setStore(store);
                for (WorkerRequirement workerRequirement : season.getWorkerRequirement()) {
                    workerRequirement.setSeason(season);
                    for (ShiftDetails shiftDetails : workerRequirement.getAllShifts()) {
                        shiftDetails.setWorkerRequirement(workerRequirement);
                        List<Shift> shiftList = new ArrayList<>();
                        for (Shift shift : shiftDetails.getShifts()) {
                            for(int i = 0; i < shift.getRequired(); i++){
                                Shift newShift = new Shift();
                                newShift.setDay(shift.getDay());
                                newShift.setStartTime(shift.getStartTime());
                                newShift.setEndTime(shift.getEndTime());
                                newShift.setRequired(1);
                                newShift.setShiftDetails(shiftDetails);
                                shiftList.add(newShift);
                            }
                        }
                        shiftDetails.setShifts(shiftList);
                    }
                }
            }
            storeRepo.save(store);
        }
    }

    public List<Store> getAllStores(){
        return storeRepo.findAll();
    }
}
