package com.rostering.employeerostering.entity;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class RequiredShifts {

    private String shift_type;
    private List<Shift> shifts;
}
