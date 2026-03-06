package com.maksim.submissionAcceptorService.dto;

import com.maksim.submissionAcceptorService.entity.ProgrammingLanguage;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class SubmissionRequestDto {
    private String sourceCode;
    private MultipartFile sourceFile;
    @NotNull
    private ProgrammingLanguage language;
}
