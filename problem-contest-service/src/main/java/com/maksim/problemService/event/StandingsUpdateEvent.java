package com.maksim.problemService.event;

import com.maksim.problemService.enums.Status;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.Instant;

@Data
@AllArgsConstructor
public class StandingsUpdateEvent {
    private Integer userId;
    private Integer contestId;
    private Integer problemId;
    private Instant submissionTime;
    private Status status;
}
