package com.maksim.problemService.dto.problem;

import com.maksim.problemService.enums.CheckerType;
import com.maksim.problemService.enums.ProgrammingLanguage;
import lombok.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.stream.Stream;

@AllArgsConstructor
@Getter
@Setter
@ToString
@NoArgsConstructor
public class SendTestCasesToJudgeServiceDto {
    private int problemId;
    private List<byte[]> testFilesContent;
    private List<String> testFilesNames;
    private int countOfTestCases;
    private CheckerType checkerType;
    private ProgrammingLanguage checkerLanguage;
    private byte[] checkerSourceCode; // optional

    public static SendTestCasesToJudgeServiceDto from(ProblemCreateDto p) {
        var target = new SendTestCasesToJudgeServiceDto();
        target.countOfTestCases = p.testCasesNum();
        target.checkerType = p.checkerType();

        if (p.checkerType() == CheckerType.CUSTOM_CHECKER) {
            try {
                target.checkerSourceCode = p.fileSourceChecker().getBytes();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            target.checkerLanguage = p.checkerLanguage();
        }

        target.testFilesContent = Stream.concat(p.inputTestCases().stream(), p.outputTestCases().stream())
                .map(file -> {
                    try {
                        return file.getBytes();
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }).toList();

        target.testFilesNames = Stream.concat(p.inputTestCases().stream(), p.outputTestCases().stream())
                .map(MultipartFile::getOriginalFilename).toList();

        return target;
    }
}
