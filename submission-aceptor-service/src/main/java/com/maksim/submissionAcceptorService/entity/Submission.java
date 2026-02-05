package com.maksim.submissionAcceptorService.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "submissions")
@Getter
@Setter
@NoArgsConstructor
public class Submission {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private long id;
    @Column(name = "user_id")
    private int userId;
    @Column(name = "problem_id")
    private int problemId;
    @Column(name = "time")
    private LocalDateTime time;
    @Column(name = "source", length = 50_000)
    private String source;
    @Column(name = "language")
    private ProgrammingLanguage programmingLanguage;
    @Column(name = "status")
    private Status status;
    @Column(name = "execution_time")
    private int executionTime;
    @Column(name = "used_memory")
    private int usedMemory;
    @Column(name = "test_num")
    private int testNum;

    public Submission(int userId, int problemId, LocalDateTime time, String source, ProgrammingLanguage programmingLanguage, Status status, int executionTime, int usedMemory, int testNum) {
        this.userId = userId;
        this.problemId = problemId;
        this.time = time;
        this.source = source;
        this.programmingLanguage = programmingLanguage;
        this.status = status;
        this.executionTime = executionTime;
        this.usedMemory = usedMemory;
        this.testNum = testNum;
    }
}
