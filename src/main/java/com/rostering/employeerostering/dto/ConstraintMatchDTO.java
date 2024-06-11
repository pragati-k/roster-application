package com.rostering.employeerostering.dto;

public class ConstraintMatchDTO {
    private String justification;
    private String score;

    public ConstraintMatchDTO(String justification, String score) {
        this.justification = justification;
        this.score = score;
    }

    public String getJustification() {
        return justification;
    }

    public String getScore() {
        return score;
    }
}

