package com.maksim.submissionAcceptorService.event;

import com.maksim.submissionAcceptorService.enums.ProgrammingLanguage;
import jakarta.annotation.Nullable;
import lombok.*;

@Data
@Builder
public class SolutionSubmittedEvent {
    private Integer problemId;
    private Integer contestId;
    private Integer userId;
    private Long submissionId;
    private String source;
    private ProgrammingLanguage language;
    private Integer timeLimit;
    private Integer memoryLimit;
    private Integer compilationTimeLimit;
}