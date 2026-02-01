package com.maksim.submissionAcceptorService.dto;
import com.maksim.submissionAcceptorService.entity.ProgrammingLanguage;
import lombok.*;


@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class SubmitSolutionTextDto {
    private int problemId;
    private String source;
    private ProgrammingLanguage language;
}