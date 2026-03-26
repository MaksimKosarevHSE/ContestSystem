package com.maksim.problemService.entity.associative;

import com.maksim.problemService.entity.Contest;
import com.maksim.problemService.entity.Problem;
import com.maksim.problemService.entity.keys.ContestProblemId;
import jakarta.persistence.*;
import lombok.Data;

import java.util.Objects;

@Table(name = "contest_problem")
@Entity
@Data
public class ContestProblem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "contest_id")
    private Contest contest;

    @ManyToOne
    @JoinColumn(name = "problem_id")
    private Problem problem;

    private int score; // баллов за задачу

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        ContestProblem that = (ContestProblem) o;
        return Objects.equals(contest, that.contest) && Objects.equals(problem, that.problem);
    }

    @Override
    public int hashCode() {
        return Objects.hash(contest, problem);
    }
}
