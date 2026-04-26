package com.maksim.problemService.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;
import java.util.Objects;

@Entity
@Table(name = "problems")
@Getter
@Setter
@NoArgsConstructor
public class Problem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "creator_id")
    private Integer creatorId;

    @Column(name = "is_public")
    private Boolean isPublic;

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
    private Integer compileTimeLimit;

    @Column(name = "time_limit")
    private Integer timeLimit;

    @Column(name = "memory_limit")
    private Integer memoryLimit;

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Problem problem = (Problem) o;
        return Objects.equals(id, problem.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }
}
