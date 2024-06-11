package com.rostering.employeerostering.dto;

import java.util.List;

public class ScoreExplanationDTO {
    private String score;
    private List<ConstraintMatchTotalDTO> constraintMatchTotals;

    public ScoreExplanationDTO(String score, List<ConstraintMatchTotalDTO> constraintMatchTotals) {
        this.score = score;
        this.constraintMatchTotals = constraintMatchTotals;
    }

    public String getScore() {
        return score;
    }

    public List<ConstraintMatchTotalDTO> getConstraintMatchTotals() {
        return constraintMatchTotals;
    }
}

