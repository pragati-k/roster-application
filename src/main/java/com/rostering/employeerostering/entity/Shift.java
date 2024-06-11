package com.rostering.employeerostering.entity;

import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalTime;
import java.util.List;
import java.util.Objects;
import java.util.Set;


@Getter
@Setter
public class Shift {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private List<String> days;

    private Set<String> skills;

    private LocalTime startTime;
    private LocalTime endTime;

    private int employee_required = 0;

    @Override
    public String toString() {
        return "Shift{" +
                "id=" + id +
                ", days=" + days +
                ", skills=" + skills +
                ", startTime=" + startTime +
                ", endTime=" + endTime +
                ", employee_required=" + employee_required +
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