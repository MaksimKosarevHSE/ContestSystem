package com.maksim.submissionAcceptorService.dto;

import com.maksim.submissionAcceptorService.entity.ProgrammingLanguage;
import com.maksim.submissionAcceptorService.entity.Status;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class GetSubmissionDto {
    private long id;
    private int userId;
    private int problemId;
    private LocalDateTime time;
    private ProgrammingLanguage programmingLanguage;
    private Status status;
    private int executionTime;
    private int usedMemory;
}
