package com.maksim.testingService.service.model;

import com.maksim.testingService.enums.Status;
import lombok.*;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class VerdictInfo {
    private Status status;
    private Integer executionTime = 0;
    private Integer usedMemory = 0;
    private Integer testNum;
    private String checkerMessage;
}
