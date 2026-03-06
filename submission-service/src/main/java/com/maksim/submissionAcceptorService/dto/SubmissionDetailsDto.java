package com.maksim.submissionAcceptorService.dto;

import com.maksim.submissionAcceptorService.entity.ProgrammingLanguage;
import com.maksim.submissionAcceptorService.entity.Status;
import jakarta.annotation.Nullable;
import jakarta.persistence.Column;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class SubmissionDetailsDto {
    private long id;
    private int userId;
    private int problemId;
    private Integer contestId;
    private boolean sentWhileContest;
    private LocalDateTime time;
    private String source;
    private ProgrammingLanguage programmingLanguage;
    private Status status;
    private int executionTime;
    private int usedMemory;
    private int testNum;
    private String input;
    private String output;
    private String checkerComment;
}
