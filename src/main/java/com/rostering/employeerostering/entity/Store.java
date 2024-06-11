package com.rostering.employeerostering.entity;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalTime;
import java.util.List;

@Getter
@Setter
public class Store {
    private String name;
    private List<LocalTime> store_hours;
    private List<RequiredShifts> required_shifts;


}
