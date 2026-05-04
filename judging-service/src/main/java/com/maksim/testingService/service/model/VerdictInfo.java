package com.maksim.testingService.service.model;

import com.maksim.common.enums.Status;
import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class VerdictInfo {
    private Status status;
    private Integer executionTime;
    private Integer memory;
    private Integer testNum;
    private String checkerMessage;
}
