package com.maksim.submissionAcceptorService.service.DTO;
import com.maksim.submissionAcceptorService.ProgrammingLanguage;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class SubmitSolutionDto{
    private String source;
    private ProgrammingLanguage language;
    private int problemId;
}