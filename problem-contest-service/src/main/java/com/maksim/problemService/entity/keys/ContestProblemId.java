package com.maksim.problemService.entity.keys;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.Data;

import java.io.Serializable;

@Embeddable
@Data
public class ContestProblemId implements Serializable {
    @Column(name = "contest_id")
    private Integer contestId;
    @Column(name = "problem_id")
    private Integer problemId;
}
