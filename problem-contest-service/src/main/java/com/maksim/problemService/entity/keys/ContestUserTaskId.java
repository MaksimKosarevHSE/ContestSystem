package com.maksim.problemService.entity.keys;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Embeddable
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ContestUserTaskId {
    @Column(name = "contest_id")
    private int contestId;

    @Column(name = "user_id")
    private int userId;

    @Column(name = "task_id")
    private int taskId;
}
