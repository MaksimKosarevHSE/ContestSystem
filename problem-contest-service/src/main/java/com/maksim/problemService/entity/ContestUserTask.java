package com.maksim.problemService.entity;

import com.maksim.problemService.entity.keys.ContestUserTaskId;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "contest_user_task")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ContestUserTask {
    @EmbeddedId
    private ContestUserTaskId id;

    @ManyToOne
    @MapsId("contestId")
    @JoinColumn(name = "contest_id")
    private Contest contest;

    @ManyToOne
    @MapsId("taskId")
    @JoinColumn(name = "task_id")
    private Problem problem;

    private Boolean isSolved;
    private int attempts;
    private int score;
//    private int fine;
    private LocalDateTime solutionTime;

    public ContestUserTask(ContestUserTaskId id) {
        this.id = id;
        this.isSolved = false;
    }

//    public void addFine(int x){
//        fine += x;
//    }

    public void incAttempts(){
        attempts++;
    }



}
