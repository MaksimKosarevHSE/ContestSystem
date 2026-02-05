package com.maksim.testingService.event;

import com.maksim.testingService.enums.Status;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

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
