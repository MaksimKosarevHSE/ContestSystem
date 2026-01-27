package testing.DTO;
import enums.ProgrammingLanguage;

public class SubmissionMetaDTO{
    private int submissionId;
    private int problemId;
    private Integer contestId;
    private String sourceCode;
    private ProgrammingLanguage programmingLanguage;
    private int timeLimit;
    private int memoryLimit;

    public SubmissionMetaDTO(int submissionId, int problemId, Integer contestId, String sourceCode, ProgrammingLanguage programmingLanguage, int timeLimit, int memoryLimit) {
        this.submissionId = submissionId;
        this.problemId = problemId;
        this.contestId = contestId;
        this.sourceCode = sourceCode;
        this.programmingLanguage = programmingLanguage;
        this.timeLimit = timeLimit;
        this.memoryLimit = memoryLimit;
    }

    public int getSubmissionId() {
        return submissionId;
    }

    public void setSubmissionId(int submissionId) {
        this.submissionId = submissionId;
    }

    public int getProblemId() {
        return problemId;
    }

    public void setProblemId(int problemId) {
        this.problemId = problemId;
    }

    public Integer getContestId() {
        return contestId;
    }

    public void setContestId(Integer contestId) {
        this.contestId = contestId;
    }

    public String getSourceCode() {
        return sourceCode;
    }

    public void setSourceCode(String sourceCode) {
        this.sourceCode = sourceCode;
    }

    public ProgrammingLanguage getProgrammingLanguage() {
        return programmingLanguage;
    }

    public void setProgrammingLanguage(ProgrammingLanguage programmingLanguage) {
        this.programmingLanguage = programmingLanguage;
    }

    public int getTimeLimit() {
        return timeLimit;
    }

    public void setTimeLimit(int timeLimit) {
        this.timeLimit = timeLimit;
    }

    public int getMemoryLimit() {
        return memoryLimit;
    }

    public void setMemoryLimit(int memoryLimit) {
        this.memoryLimit = memoryLimit;
    }
}