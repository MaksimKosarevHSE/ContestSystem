package com.maksim.problemService.dto.problem;

import jakarta.annotation.Nullable;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class ProblemConstraints {
    private int id;
    private double compileTimeLimit;
    private double timeLimit;
    private double memoryLimit;
    // если задача в не в проблем сете, а в контесте
    @Nullable
    private Integer contestId;
    @Nullable
    private LocalDateTime contestStartTime;
    @Nullable
    private LocalDateTime contestEndTime;

    public ProblemConstraints(int id, double compileTimeLimit, double timeLimit, double memoryLimit) {
        this.id = id;
        this.compileTimeLimit = compileTimeLimit;
        this.timeLimit = timeLimit;
        this.memoryLimit = memoryLimit;
    }
}
