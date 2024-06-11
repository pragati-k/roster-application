package com.rostering.employeerostering.entity;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class Store {
    private String name;
    private List<Shift> required_shifts;

}
