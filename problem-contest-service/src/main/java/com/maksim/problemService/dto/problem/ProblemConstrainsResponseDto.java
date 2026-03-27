package com.maksim.problemService.dto.problem;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ProblemConstrainsResponseDto {
    private Integer id;
    private Integer compileTimeLimit;
    private Integer timeLimit;
    private Integer memoryLimit;
    private Integer contestId;
    private LocalDateTime contestStartTime;
    private LocalDateTime contestEndTime;
}