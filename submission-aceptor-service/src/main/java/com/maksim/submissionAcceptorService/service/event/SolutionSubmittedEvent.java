package com.maksim.submissionAcceptorService.service.event;

import com.maksim.submissionAcceptorService.ProgrammingLanguage;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@AllArgsConstructor
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
}