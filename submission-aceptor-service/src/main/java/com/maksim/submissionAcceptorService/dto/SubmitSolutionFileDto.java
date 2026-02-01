package com.maksim.submissionAcceptorService.dto;

import com.maksim.submissionAcceptorService.entity.ProgrammingLanguage;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class SubmitSolutionFileDto {
    private int problemId;
    private MultipartFile source;
    private ProgrammingLanguage language;

}
