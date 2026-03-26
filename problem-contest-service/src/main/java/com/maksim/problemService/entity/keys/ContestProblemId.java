package com.maksim.problemService.entity.keys;

import com.maksim.problemService.entity.associative.ContestProblem;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Embeddable
@Data
@NoArgsConstructor
public class ContestProblemId implements Serializable {
    @Column(name = "contest_id")
    private Integer contestId;
    @Column(name = "problem_id")
    private Integer problemId;

    public ContestProblemId(Integer contestId, Integer problemId){
        this.contestId = contestId;
        this.problemId = problemId;
    }
}
