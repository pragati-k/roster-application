package com.rostering.employeerostering.entity;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Objects;


@Getter
@Setter
public class Shift {
    private int id;
    private List<String> days;
    private LocalTime startTime;
    private LocalTime endTime;
    private int required;


    @Override
    public String toString() {
        return "Shift{" +
                "id=" + id +
                ", days=" + days +
                ", startTime=" + startTime +
                ", endTime=" + endTime +
                ", required=" + required +
                '}';
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null || getClass() != obj.getClass())
            return false;

        Shift other = (Shift) obj;

        // Compare fields for equality
        return Objects.equals(endTime, other.endTime) && Objects.equals(startTime, other.startTime);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, startTime, endTime);
    }
}