package com.maksim.testingService.entity;

import java.util.List;

public class Problem {
    private int id;
    private Integer contestId;
    private int authorId;
    private String title;
    private String statement;
    private String input;
    private String output;
    private String notes;
    private int memoryLimit;
    private int timeLimit;
    private List<String> inputSample;
    private List<String> outputSample;

    public Problem(int id, Integer contestId, int authorId, String title, String statement, String input, String output, String notes, int memoryLimit, int timeLimit, List<String> inputSample, List<String> outputSample) {
        this.id = id;
        this.contestId = contestId;
        this.authorId = authorId;
        this.title = title;
        this.statement = statement;
        this.notes = notes;
        this.memoryLimit = memoryLimit;
        this.timeLimit = timeLimit;
        this.inputSample = inputSample;
        this.outputSample = outputSample;
        this.input = input;
        this.output = output;
    }

    public int getAuthorId() {
        return authorId;
    }

    public void setAuthorId(int authorId) {
        this.authorId = authorId;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Integer getContestId() {
        return contestId;
    }

    public void setContestId(Integer contestId) {
        this.contestId = contestId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getStatement() {
        return statement;
    }

    public void setStatement(String statement) {
        this.statement = statement;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public int getMemoryLimit() {
        return memoryLimit;
    }

    public void setMemoryLimit(int memoryLimit) {
        this.memoryLimit = memoryLimit;
    }

    public int getTimeLimit() {
        return timeLimit;
    }

    public void setTimeLimit(int timeLimit) {
        this.timeLimit = timeLimit;
    }

    public List<String> getInputSample() {
        return inputSample;
    }

    public void setInputSample(List<String> inputSample) {
        this.inputSample = inputSample;
    }

    public List<String> getOutputSample() {
        return outputSample;
    }

    public void setOutputSample(List<String> outputSample) {
        this.outputSample = outputSample;
    }

    public String getOutput() {
        return output;
    }

    public void setOutput(String output) {
        this.output = output;
    }

    public String getInput() {
        return input;
    }

    public void setInput(String input) {
        this.input = input;
    }
}


