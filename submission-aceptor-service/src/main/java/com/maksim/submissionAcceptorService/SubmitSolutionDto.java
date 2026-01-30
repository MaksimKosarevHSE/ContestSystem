package com.maksim.submissionAcceptorService;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


//@Setter
//@Getter
//@NoArgsConstructor
//@AllArgsConstructor
public class SubmitSolutionDto{
    private String source;
    private ProgrammingLanguage language;
    private int problemId;

    public SubmitSolutionDto() {
    }

    public SubmitSolutionDto(String source, ProgrammingLanguage language, int problemId) {
        this.source = source;
        this.language = language;
        this.problemId = problemId;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public ProgrammingLanguage getLanguage() {
        return language;
    }

    public void setLanguage(ProgrammingLanguage language) {
        this.language = language;
    }

    public int getProblemId() {
        return problemId;
    }

    public void setProblemId(int problemId) {
        this.problemId = problemId;
    }
}