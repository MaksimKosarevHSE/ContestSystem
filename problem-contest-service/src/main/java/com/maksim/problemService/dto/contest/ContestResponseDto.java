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
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private List<ProblemSignatureResponseDto> problems;
}
