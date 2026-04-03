package com.maksim.problemService.entity.associative;

import com.maksim.problemService.entity.Contest;
import com.maksim.problemService.entity.Problem;
import com.maksim.problemService.entity.keys.ContestUserTaskId;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;


@Entity
@Table(name = "contest_user_task")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ContestUserTask {
    @EmbeddedId
    private ContestUserTaskId id = new ContestUserTaskId();

    @ManyToOne
    @MapsId("contestId")
    @JoinColumn(name = "contest_id")
    private Contest contest;

    @ManyToOne
    @MapsId("taskId")
    @JoinColumn(name = "task_id")
    private Problem problem;

    @Column(name = "solved")
    private Boolean solved;

    @Column(name = "attempts")
    private Integer attempts;

    @Column(name = "score")
    private Integer score;

    @Column(name = "solution_time")
    private Instant solutionTime;

    public ContestUserTask(ContestUserTaskId id) {
        this.id = id;
    }

    public void incAttempts() {
        attempts++;
    }


}
