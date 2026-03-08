package com.maksim.problemService.dto.problem;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.maksim.problemService.entity.CheckerType;
import com.maksim.problemService.entity.ProgrammingLanguage;
import jakarta.validation.constraints.*;
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
    @NotBlank(message = "Title of problem is required")
    @Size(max = 50, message = "Title length must be from 1 to 50 symbol")
    private String title;

    @NotNull
    private boolean isPublic;

    @NotBlank(message = "Statement of problem is required")
    @Size(max = 5000, message = "Statement length must be from 1 to 5000 symbols")
    private String statement;

    @Size(max = 5000, message = "Input length must be from 0 to 5000 symbols")
    private String input;

    @Size(max = 5000, message = "Output length must be from 0 to 5000 symbols")
    private String output;

    @Size(max = 5000, message = "Notes length must be from 0 to 5000 symbols")
    private String notes;

    @Min(value = 0, message = "Samples count must be not negative")
    @Max(value = 15, message = "Samples count must be less than 15")
    private int samplesCount;

    // валидация в ProblemValidator
    private List<String> sampleInput;
    private List<String> sampleOutput;

    @Min(value = 1, message = "Complexity must be greater than 0 and less than 11")
    @Max(value = 10, message = "Complexity must be greater than 0 and less than 11")
    private int complexity;

    @Positive
    @Max(value = 10)
    private double compileTimeLimit;

    @Positive
    @Max(value = 10)
    private double timeLimit;

    @Positive
    @Max(value = 2048)
    private double memoryLimit;

    @NotNull
    private CheckerType checkerType;

    @NotNull
    private ProgrammingLanguage checkerLanguage;

    // валидация в ProblemValidator
    @JsonIgnore
    private MultipartFile fileSourceChecker; //optional

    @Positive
    private int testCasesNum;

    // валидация в ProblemValidator
    @JsonIgnore
    private List<MultipartFile> inputTestCases; // must be numbered from 1 to n

    @JsonIgnore
    private List<MultipartFile> outputTestCases; // must be numbered from 1 to n
}



