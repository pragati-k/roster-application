package com.rostering.employeerostering.dto;

import java.util.List;

public class ConstraintMatchTotalDTO {
    private String constraintName;
    private String score;
    private List<ConstraintMatchDTO> constraintMatches;

    public ConstraintMatchTotalDTO(String constraintName, String score, List<ConstraintMatchDTO> constraintMatches) {
        this.constraintName = constraintName;
        this.score = score;
        this.constraintMatches = constraintMatches;
    }

    public String getConstraintName() {
        return constraintName;
    }

    public String getScore() {
        return score;
    }

    public List<ConstraintMatchDTO> getConstraintMatches() {
        return constraintMatches;
    }
}

