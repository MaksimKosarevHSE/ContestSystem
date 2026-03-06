package com.maksim.submissionAcceptorService.dto;

import jakarta.annotation.Nullable;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class ProblemConstraintsDto {
    private int id;
    private double compileTimeLimit;
    private double timeLimit;
    private double memoryLimit;
    // if problem is included in contest
    @Nullable
    private Integer contestId;
    @Nullable
    private LocalDateTime contestStartTime;
    @Nullable
    private LocalDateTime contestEndTime;
}
