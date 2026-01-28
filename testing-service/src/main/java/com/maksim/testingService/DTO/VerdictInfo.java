package com.maksim.testingService.DTO;

import com.maksim.testingService.enums.Status;
import lombok.*;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class VerdictInfo{
    private Status status;
    private int executionTime = 0;
    private int usedMemory = 0;
    private int numOfFailureTest;
    private String input;
    private String output;
    private String verdictMsg;
}
