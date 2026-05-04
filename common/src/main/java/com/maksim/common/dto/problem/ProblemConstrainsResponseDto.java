package com.maksim.common.dto.problem;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProblemConstrainsResponseDto {
    private Integer id;
    private Integer compileTimeLimit;
    private Integer timeLimit;
    private Integer memoryLimit;
    private Integer contestId;
    private Instant contestStartTime;
    private Instant contestEndTime;
}
