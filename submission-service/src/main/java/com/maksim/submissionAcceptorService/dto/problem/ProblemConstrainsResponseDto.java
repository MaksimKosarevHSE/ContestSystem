package com.maksim.submissionAcceptorService.dto.problem;

import java.time.LocalDateTime;

public record ProblemConstrainsResponseDto(
        Integer id,
        Integer compileTimeLimit,
        Integer timeLimit,
        Integer memoryLimit,
        Integer contestId,
        LocalDateTime contestStartTime,
        LocalDateTime contestEndTime
) {
}