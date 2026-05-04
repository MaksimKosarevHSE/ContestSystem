package com.maksim.common.event;

import com.maksim.common.enums.Status;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SolutionJudgedEvent {
    private Long submissionId;
    private Status status;
    private Integer testNum;
    private Integer memory;
    private Integer executionTime;
    private String checkerMessage;
}
