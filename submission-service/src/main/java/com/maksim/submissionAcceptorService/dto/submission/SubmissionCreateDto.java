package com.maksim.submissionAcceptorService.dto.submission;

import com.maksim.submissionAcceptorService.enums.ProgrammingLanguage;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import org.springframework.web.multipart.MultipartFile;


@Schema(description = "Body for new submission")
public record SubmissionCreateDto(
        @Schema(description = "Source code (text)", example = "print('Hello world!')")
        String sourceCode,

        @Schema(description = "Source code (file)")
        MultipartFile sourceFile,

        @NotNull
        @Schema(description = "Programming language", example = "CPP")
        ProgrammingLanguage language
){

}