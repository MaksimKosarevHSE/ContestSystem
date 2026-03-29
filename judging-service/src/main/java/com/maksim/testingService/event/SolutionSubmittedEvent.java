package com.maksim.testingService.event;


import com.maksim.testingService.enums.ProgrammingLanguage;


public record SolutionSubmittedEvent(
        Integer problemId,
        Integer contestId,
        Integer userId,
        Long submissionId,
        String source,
        ProgrammingLanguage language,
        Integer timeLimit,
        Integer memoryLimit,
        Integer compilationTimeLimit
) {
}