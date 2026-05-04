package com.maksim.common.event;

import com.maksim.common.enums.Status;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StandingsUpdateEvent {
    private Integer userId;
    private Integer contestId;
    private Integer problemId;
    private Instant submissionTime;
    private Status status;
}
