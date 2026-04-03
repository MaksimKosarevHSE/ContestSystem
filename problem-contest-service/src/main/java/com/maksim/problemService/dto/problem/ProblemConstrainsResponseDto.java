package com.maksim.problemService.dto.problem;

import lombok.Data;

import java.time.Instant;

@Data
public class ProblemConstrainsResponseDto {
    private Integer id;
    private Integer compileTimeLimit;
    private Integer timeLimit;
    private Integer memoryLimit;
    private Integer contestId;
    private Instant contestStartTime;
    private Instant contestEndTime;
}