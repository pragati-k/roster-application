package com.rostering.employeerostering.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;

@Entity
@Getter
@Setter
public class Seasons {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private String type;

    private LocalDate seasonStartDate;
    private LocalDate seasonEndDate;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "exception_dates", joinColumns = @JoinColumn(name = "season_id"))
    @Column(name = "exception_date")
    private List<LocalDate> exceptionDates;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "special_dates", joinColumns = @JoinColumn(name = "season_id"))
    @Column(name = "special_date")
    private List<LocalDate> specialDates;

    @OneToMany(mappedBy = "season", cascade = CascadeType.ALL)
    private List<WorkerRequirement> workerRequirement;

    @ManyToOne
    @JoinColumn(name = "store_id")
    @JsonIgnore
    private Store store;
}
