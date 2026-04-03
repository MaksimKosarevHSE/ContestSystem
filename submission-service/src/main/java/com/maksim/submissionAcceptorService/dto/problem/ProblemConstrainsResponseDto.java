package com.maksim.submissionAcceptorService.dto.problem;

import java.time.Instant;

public record ProblemConstrainsResponseDto(
        Integer id,
        Integer compileTimeLimit,
        Integer timeLimit,
        Integer memoryLimit,
        Integer contestId,
        Instant contestStartTime,
        Instant contestEndTime
) {
}