package com.maksim.problemService.dto.problem;

import com.maksim.common.dto.problem.SaveTestCasesRequestDto;
import com.maksim.common.enums.CheckerType;
import com.maksim.common.enums.ProgrammingLanguage;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.stream.Stream;

public record ProblemCreateDto(

        @Schema(description = "Title of problem")
        @NotBlank(message = "Title of problem is required")
        @Size(max = 50, message = "Title length must be from 1 to 50 symbol")
        String title,

        @Schema(description = "Can this problem be displayed in public problem set?")
        @NotNull
        Boolean isPublic,

        @Schema(description = "Problem statement")
        @NotBlank(message = "Statement of problem is required")
        @Size(max = 5000, message = "Statement length must be from 1 to 5000 symbols")
        String statement,

        @Schema(description = "Input description")
        @Size(max = 5000, message = "Input length must be from 0 to 5000 symbols")
        String input,

        @Schema(description = "Output description")
        @Size(max = 5000, message = "Output length must be from 0 to 5000 symbols")
        String output,

        @Schema(description = "Notes to problem")
        @Size(max = 5000, message = "Notes length must be from 0 to 5000 symbols")
        String notes,

        @Schema(description = "Count of samples")
        @Min(value = 0, message = "Samples count must be not negative")
        @Max(value = 15, message = "Samples count must be less than 15")
        Integer samplesCount,

        @Schema(description = "i-th array's element representations i-th sample input")
        List<String> sampleInput,

        @Schema(description = "i-th array's element representations i-th sample output")
        List<String> sampleOutput,

        @Schema(description = "Estimated by you complexity of this problem")
        @Min(value = 1, message = "Complexity must be greater than 0 and less than 11")
        @Max(value = 10, message = "Complexity must be greater than 0 and less than 11")
        Integer complexity,

        @Schema(description = "Compile time limit in ms")
        @Positive @Max(value = 10000)
        Double compileTimeLimit,

        @Schema(description = "Time limit in ms")
        @Positive @Max(value = 10000)
        Double timeLimit,

        @Schema(description = "Memory limit for solutions")
        @Positive @Max(value = 1048576)
        Double memoryLimit,

        @Schema(description = "Type of checker program")
        @NotNull
        CheckerType checkerType,

        @Schema(description = "Language of custom checker (source code language)")
        ProgrammingLanguage checkerLanguage,

        @Schema(description = "Custom checker's source code")
        MultipartFile fileSourceChecker,

        @Schema(description = "Number of tests")
        @Positive
        Integer testCasesNum,

        @Schema(description = "Input for test in files. This files must be numbered from 1.in to N.in, where N is number of test cases")
        List<MultipartFile> inputTestCases,

        @Schema(description = "Output for test in files. This files must be numbered from 1.out to N.out, where N is number of test cases")
        List<MultipartFile> outputTestCases
) {
    public SaveTestCasesRequestDto toSaveTestCasesRequestDto() {
        byte[] checkerSource = null;
        if (checkerType == CheckerType.CUSTOM_CHECKER) {
            checkerSource = readBytes(fileSourceChecker, "Failed to read checker source code");
        }

        List<byte[]> testFilesContent = Stream.concat(inputTestCases.stream(), outputTestCases.stream())
                .map(file -> readBytes(file, "Failed to read test case file"))
                .toList();
        List<String> testFilesNames = Stream.concat(inputTestCases.stream(), outputTestCases.stream())
                .map(MultipartFile::getOriginalFilename)
                .toList();

        return new SaveTestCasesRequestDto(
                null,
                testFilesContent,
                testFilesNames,
                testCasesNum,
                checkerType,
                checkerLanguage,
                checkerSource
        );
    }

    private static byte[] readBytes(MultipartFile file, String errorMessage) {
        try {
            return file.getBytes();
        } catch (IOException e) {
            throw new IllegalStateException(errorMessage, e);
        }
    }
}
