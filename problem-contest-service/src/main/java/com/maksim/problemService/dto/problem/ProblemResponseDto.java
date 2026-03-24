package com.maksim.problemService.dto.problem;

import com.maksim.problemService.enums.CheckerType;
import com.maksim.problemService.enums.ProgrammingLanguage;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class ProblemResponseDto {
    private Integer id;
    private String title;
    private String statement;
    private String input;
    private String output;
    private String notes;
    private Integer samplesCount;
    private List<String> sampleInput;
    private List<String> sampleOutput;
    private Integer complexity;
    private Integer compileTimeLimit;
    private Integer timeLimit;
    private Integer memoryLimit;
    private Integer creatorId;
    private Boolean isPublic;
}