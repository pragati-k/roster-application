package com.rostering.employeerostering.service;

import com.rostering.employeerostering.entity.DateShift;
import com.rostering.employeerostering.entity.Shift;
import com.rostering.employeerostering.entity.WorkerRequirement;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
public class ShiftGenerator {

    public static List<DateShift> generateShifts(LocalDate startDate, LocalDate endDate, WorkerRequirement workerRequirement) {
        List<DateShift> shifts = new ArrayList<>();

        // Iterate over each date in the range
        for (LocalDate date = startDate; !date.isAfter(endDate); date = date.plusDays(1)) {
            String dayOfWeek = date.getDayOfWeek().name().toUpperCase();
                for(Shift shift: workerRequirement.getShift()){
                    if(shift.getDays().contains(dayOfWeek)){
                        DateShift dateShift = new DateShift();
                        dateShift.setType(workerRequirement.getType());
                        dateShift.setDate(date);
                        dateShift.setId(shift.getId());
                        dateShift.setRequired(shift.getRequired());
                        dateShift.setDay(dayOfWeek);
                        dateShift.setStartTime(shift.getStartTime());
                        dateShift.setEndTime(shift.getEndTime());
                        shifts.add(dateShift);
                    }
                }

            }

        return shifts;
    }

    public static List<DateShift> start(WorkerRequirement workerRequirement) {
        LocalDate startDate = LocalDate.of(2024, 6, 17);
        LocalDate endDate = LocalDate.of(2024, 6, 22);

        List<DateShift> shifts = generateShifts(startDate, endDate, workerRequirement);
        return shifts;
    }
}
