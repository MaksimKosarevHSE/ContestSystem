package com.maksim.submissionAcceptorService.entity;

import com.maksim.submissionAcceptorService.enums.ProgrammingLanguage;
import com.maksim.submissionAcceptorService.enums.Status;
import jakarta.annotation.Nullable;
import jakarta.persistence.*;
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
    @Column(name = "contest_id")
    @Nullable
    private Integer contestId;
    @Column(name = "is_upsolving")
    private Boolean isUpsolving;
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

    public Submission(int userId, int problemId, Integer contestId, LocalDateTime time, String source, ProgrammingLanguage programmingLanguage, Status status, int executionTime, int usedMemory, int testNum) {
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
