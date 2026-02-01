package com.maksim.problemService.dto;

import com.maksim.problemService.entity.CheckerType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class SendTestCasesToJudgeServiceDto {
    private List<byte[]> testFilesContent;
    private List<String> testFilesNames;
    private int countOfTestCases;
    private CheckerType checkerType;
    private byte[] checkerSourceCode; // optional
}
