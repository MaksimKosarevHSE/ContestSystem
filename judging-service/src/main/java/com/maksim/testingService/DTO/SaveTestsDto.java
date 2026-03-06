package com.maksim.testingService.DTO;

import com.maksim.testingService.entity.CheckerType;
import com.maksim.testingService.enums.ProgrammingLanguage;
import lombok.*;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
public class SaveTestsDto {
    private int problemId;
    private List<byte[]> testFilesContent;
    private List<String> testFilesNames;
    private int countOfTestCases;
    private CheckerType checkerType;
    private ProgrammingLanguage checkerLanguage;
    private byte[] checkerSourceCode; // optional

}
