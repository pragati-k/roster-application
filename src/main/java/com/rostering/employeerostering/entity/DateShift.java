package com.rostering.employeerostering.entity;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Getter
@Setter
public class DateShift {

    private int id;
    private String day;
    private LocalTime startTime;
    private LocalTime endTime;
    private int required;
    private String type;
    private LocalDate date;

    public DateShift(int id, String type, String day, LocalTime startTime, LocalTime endTime, LocalDate date) {
        this.id = id;
        this.type = type;
        this.day = day;
        this.startTime = startTime;
        this.endTime = endTime;
        this.date = date;
    }

    public DateShift() {

    }

    @Override
    public String toString() {
        return "DateShift{" +
                "id=" + id +
                ", day='" + day + '\'' +
                ", startTime=" + startTime +
                ", endTime=" + endTime +
                ", required=" + required +
                ", type='" + type + '\'' +
                ", date=" + date +
                '}';
    }
}
