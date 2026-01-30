package com.maksim.submissionAcceptorService.service.event;

import com.maksim.submissionAcceptorService.ProgrammingLanguage;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


//@AllArgsConstructor
@Getter
@Setter
@NoArgsConstructor
public class SolutionSubmittedEvent {
    private int problemId;
    private int userId;
    private int submissionId;
    private String source;
    private ProgrammingLanguage language;
    private int timeLimit;
    private int memoryLimit;
    private int compilationTimeLimit;

    public SolutionSubmittedEvent(int problemId, int userId, int submissionId, String source, ProgrammingLanguage language, int timeLimit, int memoryLimit, int compilationTimeLimit) {
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