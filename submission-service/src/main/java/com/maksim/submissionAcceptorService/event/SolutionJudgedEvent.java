package com.maksim.submissionAcceptorService.event;

import com.maksim.submissionAcceptorService.enums.Status;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SolutionJudgedEvent {
    private long submissionId;
    private Status status;
    private int testNum;
    private int memory;
    private int executionTime;
}
