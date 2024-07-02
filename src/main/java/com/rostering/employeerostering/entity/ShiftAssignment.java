package com.rostering.employeerostering.entity;

import com.rostering.employeerostering.dto.EmployeeDTO;
import lombok.Getter;
import lombok.Setter;
import org.optaplanner.core.api.domain.entity.PlanningEntity;
import org.optaplanner.core.api.domain.lookup.PlanningId;
import org.optaplanner.core.api.domain.variable.PlanningVariable;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;


@Getter
@Setter
@PlanningEntity
public class ShiftAssignment {

    @PlanningId
    private Integer id;

    private DateShift dateShift;

    private LocalDate startDate;
    private LocalDate endDate;

    private int store_id;

    private List<EmployeePair> requiredPairs;

    @PlanningVariable(valueRangeProviderRefs = "employeeRange")
    private EmployeeDTO employee ;

    public String getShiftTimingType() {
        LocalTime startTime = dateShift.getStartTime();
        if (startTime.isBefore(LocalTime.of(14,00,00))) {
            return "MORNING";
        } else {
            return "AFTERNOON";
        }
    }
    @Override
    public String toString() {
        return "ShiftAssignment{" +
                "id=" + id +
                ", dateShift=" + dateShift +
                ", store_name='" + store_id + '\'' +
                ", employee=" + employee +
                '}';
    }
}
