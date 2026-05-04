package com.maksim.common.event;

import com.maksim.common.enums.ProgrammingLanguage;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
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
