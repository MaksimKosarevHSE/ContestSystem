package com.maksim.testingService.dto;

import com.maksim.testingService.enums.CheckerType;
import com.maksim.testingService.enums.ProgrammingLanguage;

import java.util.List;


public record SaveTestCasesRequestDto(
        Integer problemId,
        List<byte[]> testFilesContent,
        List<String> testFilesNames,
        Integer countOfTestCases,
        CheckerType checkerType,
        ProgrammingLanguage checkerLanguage,
        byte[] checkerSourceCode
) {
}
