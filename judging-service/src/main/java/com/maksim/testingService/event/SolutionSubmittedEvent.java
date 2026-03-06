package com.maksim.testingService.event;


import com.maksim.testingService.enums.ProgrammingLanguage;
import lombok.*;


@AllArgsConstructor
@Getter
@Setter
@NoArgsConstructor
@ToString
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