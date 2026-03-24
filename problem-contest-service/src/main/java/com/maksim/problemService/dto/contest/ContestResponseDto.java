package com.maksim.problemService.dto.contest;

import com.maksim.problemService.dto.problem.ProblemSignatureResponseDto;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class ContestResponseDto {
    private Integer id;

    private String title;

    private Integer authorId;

    private String authorHandle;

    private LocalDateTime startTime;

    private LocalDateTime endTime;

    private Boolean isPublic;

    private List<ProblemSignatureResponseDto> problems;
}