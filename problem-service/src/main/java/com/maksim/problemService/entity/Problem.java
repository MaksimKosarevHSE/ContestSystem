package com.maksim.problemService.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Entity
@Table(name = "problems")
@Getter
@Setter
@NoArgsConstructor
public class Problem {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Integer id;
    @Column(name = "creator_id")
    private Integer creatorId;
    @Column(name = "title")
    private String title;
    @Column(name = "statement")
    private String statement;
    @Column(name = "input")
    private String input;
    @Column(name = "output")
    private String output;
    @Column(name = "notes")
    private String notes;
    @Column(name = "sample_count")
    private Integer samplesCount;
    @Column(name = "sample_input")
    private List<String> sampleInput;
    @Column(name = "sample_output")
    private List<String> sampleOutput;
    @Column(name = "complexity")
    private Integer complexity;
    @Column(name = "compile_time_limit")
    private Double compileTimeLimit;
    @Column(name = "time_limit")
    private Double timeLimit;
    @Column(name = "memory_limit")
    private Double memoryLimit;

    public Problem(int creatorId, String title,
                   String statement, String input,
                   String output, String notes,
                   List<String> sampleInput, List<String> sampleOutput,
                   int complexity, double compileTimeLimit,
                   double timeLimit, double memoryLimit) {
        this.creatorId = creatorId;
        this.title = title;
        this.statement = statement;
        this.input = input;
        this.output = output;
        this.notes = notes;
        this.sampleInput = sampleInput;
        this.sampleOutput = sampleOutput;
        this.complexity = complexity;
        this.compileTimeLimit = compileTimeLimit;
        this.timeLimit = timeLimit;
        this.memoryLimit = memoryLimit;
    }
}
