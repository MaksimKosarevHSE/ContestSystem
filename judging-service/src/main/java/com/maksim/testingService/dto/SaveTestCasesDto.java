package com.maksim.testingService.dto;

import com.maksim.testingService.enums.CheckerType;
import com.maksim.testingService.enums.ProgrammingLanguage;
import lombok.*;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
public class SaveTestCasesDto {
    private int problemId;
    private List<byte[]> testFilesContent;
    private List<String> testFilesNames;
    private int countOfTestCases;
    private CheckerType checkerType;
    private ProgrammingLanguage checkerLanguage;
    private byte[] checkerSourceCode; // optional

}
