package com.rostering.employeerostering.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Objects;


@Entity
@Getter
@Setter
public class Shift {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "shift_days", joinColumns = @JoinColumn(name = "shift_id"))
    @Column(name = "day")
    private List<Integer> day;

    private LocalTime startTime;
    private LocalTime endTime;
    private int required;

    @ManyToOne
    @JoinColumn(name = "shift_details_id")
    @JsonIgnore
    private ShiftDetails shiftDetails;


    @Override
    public String toString() {
        return "Shift{" +
                "id=" + id +
                ", days=" + day +
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