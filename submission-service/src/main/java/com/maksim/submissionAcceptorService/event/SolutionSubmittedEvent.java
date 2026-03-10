package com.maksim.submissionAcceptorService.event;

import com.maksim.submissionAcceptorService.enums.ProgrammingLanguage;
import jakarta.annotation.Nullable;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SolutionSubmittedEvent {
    private int problemId;
    @Nullable
    private Integer contestId;
    private int userId;
    private long submissionId;
    private String source;
    private ProgrammingLanguage language;
    private double timeLimit;
    private double memoryLimit;
    private double compilationTimeLimit;

}