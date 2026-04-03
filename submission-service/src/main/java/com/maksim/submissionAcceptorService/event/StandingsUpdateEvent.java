package com.maksim.submissionAcceptorService.event;

import com.maksim.submissionAcceptorService.enums.Status;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.time.Instant;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@Builder
public class StandingsUpdateEvent {
    private Integer userId;
    private Integer contestId;
    private Integer problemId;
    private Instant submissionTime;
    private Status status;
}
