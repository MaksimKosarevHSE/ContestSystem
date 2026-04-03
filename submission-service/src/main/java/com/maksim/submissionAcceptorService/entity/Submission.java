package com.maksim.submissionAcceptorService.entity;

import com.maksim.submissionAcceptorService.enums.ProgrammingLanguage;
import com.maksim.submissionAcceptorService.enums.Status;
import jakarta.annotation.Nullable;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.Type;

import java.time.Instant;

@Entity
@Table(name = "submissions")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Submission {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id")
    private Integer userId;

    @Column(name = "problem_id")
    private Integer problemId;

    @Column(name = "contest_id")
    private Integer contestId;

    @Column(name = "is_upsolving")
    private Boolean isUpsolving;

    @Column(name = "time")
    private Instant time;

    @Column(name = "source")
    private String source;

    @Column(name = "language")
    @Enumerated(EnumType.STRING)
    private ProgrammingLanguage programmingLanguage;

    @Column(name = "status")
    @Enumerated(EnumType.STRING)
    private Status status;

    @Column(name = "execution_time")
    private Integer executionTime;

    @Column(name = "used_memory")
    private Integer usedMemory;

    @Column(name = "test_num")
    private Integer testNum;

    @Column(name = "checker_message")
    private String checkerMessage;

}
