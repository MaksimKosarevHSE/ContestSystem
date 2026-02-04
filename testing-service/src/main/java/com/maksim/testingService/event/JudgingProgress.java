package com.maksim.testingService.event;

import com.maksim.testingService.enums.Status;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class JudgingProgress {
    private long submissionId;
    private Status status;
    private int testNum;
    private LocalDateTime time;
}
