package com.maksim.testingService.entity;

import com.maksim.testingService.enums.ProgrammingLanguage;
import com.maksim.testingService.enums.Status;

import java.time.LocalTime;

public class Submission {
    private long id;
    private LocalTime submissionTime;
    private User contestant;
    private Problem problem;
    private ProgrammingLanguage language;
    private Status status;
    private int executionTime;
    private int executionMemory;

    public Submission(int id, LocalTime submissionTime, User contestant, Problem problem, ProgrammingLanguage language, Status status, int executionTime, int executionMemory) {
        this.submissionTime = submissionTime;
        this.contestant = contestant;
        this.problem = problem;
        this.language = language;
        this.status = status;
        this.executionTime = executionTime;
        this.executionMemory = executionMemory;
        this.id = id;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public LocalTime getSubmissionTime() {
        return submissionTime;
    }

    public void setSubmissionTime(LocalTime submissionTime) {
        this.submissionTime = submissionTime;
    }

    public User getContestant() {
        return contestant;
    }

    public void setContestant(User contestant) {
        this.contestant = contestant;
    }

    public Problem getProblem() {
        return problem;
    }

    public void setProblem(Problem problem) {
        this.problem = problem;
    }

    public ProgrammingLanguage getLanguage() {
        return language;
    }

    public void setLanguage(ProgrammingLanguage language) {
        this.language = language;
    }



    public int getExecutionTime() {
        return executionTime;
    }

    public void setExecutionTime(int executionTime) {
        this.executionTime = executionTime;
    }

    public int getExecutionMemory() {
        return executionMemory;
    }

    public void setExecutionMemory(int executionMemory) {
        this.executionMemory = executionMemory;
    }

    @Override
    public String toString() {
        return "entity.Submission{" +
                "id=" + id +
                ", submissionTime=" + submissionTime +
                ", contestant=" + contestant.getName() +
                ", problem=" + problem.getId() +
                ", language=" + language +
                ", status=" + status +
                ", executionTime=" + executionTime +
                ", executionMemory=" + executionMemory +
                '}';
    }

}
