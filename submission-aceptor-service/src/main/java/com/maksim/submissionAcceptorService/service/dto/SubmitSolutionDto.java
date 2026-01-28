package com.maksim.submissionAcceptorService.service.dto;
import com.maksim.submissionAcceptorService.ProgrammingLanguage;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class SubmitSolutionDto{
    private String source;
    private ProgrammingLanguage language;
    private int problemId;

}