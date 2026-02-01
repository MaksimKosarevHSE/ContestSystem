package com.maksim.problemService.dto;

import com.maksim.problemService.entity.CheckerType;
import jakarta.persistence.Column;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class ProblemCreateDto {
    private String title;
    private String statement;
    private String input;
    private String output;
    private String notes;
    private int samplesCount;
    private List<String> sampleInput;
    private List<String> sampleOutput;
    private int complexity;
    private double compileTimeLimit;
    private double timeLimit;
    private double memoryLimit;
    private CheckerType checkerType;
    private MultipartFile fileSourceChecker; //optional
    private int testCasesNum;
    private List<MultipartFile> inputTestCases; // must be numbered from 1 to n
    private List<MultipartFile> outputTestCases; // must be numbered from 1 to n
}



