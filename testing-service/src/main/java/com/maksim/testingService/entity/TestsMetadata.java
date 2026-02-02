package com.maksim.testingService.entity;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class TestsMetadata {
    private int problemId;
    private int testCount;
    private CheckerType checkerType;
}
