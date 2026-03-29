package com.maksim.testingService.service.model;


import com.maksim.testingService.dto.SaveTestCasesRequestDto;
import com.maksim.testingService.enums.CheckerType;
import com.maksim.testingService.enums.ProgrammingLanguage;
import lombok.*;





@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class TestCasesMetadata {
    private int problemId;
    private int testCount;
    private CheckerType checkerType;
    private ProgrammingLanguage checkerLanguage;
    private String checkerFileName;

    public static TestCasesMetadata from(SaveTestCasesRequestDto dto) {
        return TestCasesMetadata.builder()
                .problemId(dto.problemId())
                .testCount(dto.countOfTestCases())
                .checkerType(dto.checkerType())
                .checkerLanguage(dto.checkerLanguage())
                .build();
    }
}
