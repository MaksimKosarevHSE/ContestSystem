package com.maksim.problemService.entity;

import com.maksim.problemService.entity.keys.ContestProblemId;
import jakarta.persistence.*;
import lombok.Data;

@Table(name = "contest_problem")
@Entity
@Data
public class ContestProblem {
    @EmbeddedId
    private ContestProblemId id;

    @ManyToOne
    @MapsId("contestId")
    @JoinColumn(name = "contest_id")
    private Contest contest;

    @ManyToOne
    @MapsId("problemId")
    @JoinColumn(name = "problem_id")
    private Problem problem;

    private int score; // баллов за задачу
}
