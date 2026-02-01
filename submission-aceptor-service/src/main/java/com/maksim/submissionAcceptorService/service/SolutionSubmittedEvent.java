package com.maksim.submissionAcceptorService.service;

import com.maksim.submissionAcceptorService.entity.ProgrammingLanguage;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Getter
@Setter
@NoArgsConstructor
public class SolutionSubmittedEvent {
    private int problemId;
    private int userId;
    private long submissionId;
    private String source;
    private ProgrammingLanguage language;
    private double timeLimit;
    private double memoryLimit;
    private double compilationTimeLimit;

    public SolutionSubmittedEvent(int problemId, int userId, long submissionId, String source, ProgrammingLanguage language, double timeLimit, double memoryLimit, double compilationTimeLimit) {
        this.problemId = problemId;
        this.userId = userId;
        this.submissionId = submissionId;
        this.source = source;
        this.language = language;
        this.timeLimit = timeLimit;
        this.memoryLimit = memoryLimit;
        this.compilationTimeLimit = compilationTimeLimit;
    }
}