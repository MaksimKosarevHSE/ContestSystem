package com.maksim.problemService.dto;

import com.maksim.problemService.entity.CheckerType;
import com.maksim.problemService.entity.ProgrammingLanguage;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor
@Getter
@Setter
@ToString
public class SendTestCasesToJudgeServiceDto {
    private int problemId;
    private List<byte[]> testFilesContent;
    private List<String> testFilesNames;
    private int countOfTestCases;
    private CheckerType checkerType;
    private ProgrammingLanguage checkerLanguage;
    private byte[] checkerSourceCode; // optional

    public SendTestCasesToJudgeServiceDto(){
        this.testFilesContent = new ArrayList<>();
        this.testFilesNames = new ArrayList<>();
    }
}
