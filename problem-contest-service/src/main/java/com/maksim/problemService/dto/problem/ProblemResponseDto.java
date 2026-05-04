package com.maksim.problemService.dto.problem;

import java.util.List;

public record ProblemResponseDto(
        Integer id,
        String title,
        String statement,
        String input,
        String output,
        String notes,
        Integer samplesCount,
        List<String> sampleInput,
        List<String> sampleOutput,
        Integer complexity,
        Integer compileTimeLimit,
        Integer timeLimit,
        Integer memoryLimit,
        Integer creatorId,
        Boolean isPublic
) {
}
