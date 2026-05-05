package com.maksim.common.dto.problem;

import com.maksim.common.enums.CheckerType;
import com.maksim.common.enums.ProgrammingLanguage;

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
    public SaveTestCasesRequestDto withProblemId(Integer problemId) {
        return new SaveTestCasesRequestDto(
                problemId,
                testFilesContent,
                testFilesNames,
                countOfTestCases,
                checkerType,
                checkerLanguage,
                checkerSourceCode
        );
    }
}
