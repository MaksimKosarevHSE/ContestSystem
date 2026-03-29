package com.maksim.testingService.service.model;

import com.maksim.testingService.enums.Status;
import lombok.*;


@Data
@ToString
public class VerdictInfo {
    private Status status;
    private Integer executionTime;
    private Integer memory;
    private Integer testNum;
    private String checkerMessage;
}
