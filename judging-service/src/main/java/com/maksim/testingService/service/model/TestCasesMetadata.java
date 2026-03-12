package com.maksim.testingService.service.model;


import com.maksim.testingService.enums.CheckerType;
import com.maksim.testingService.enums.ProgrammingLanguage;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class TestCasesMetadata {
    private int problemId;
    private int testCount;
    private CheckerType checkerType;
    private ProgrammingLanguage checkerLanguage;
    private String checkerFileName;
}
