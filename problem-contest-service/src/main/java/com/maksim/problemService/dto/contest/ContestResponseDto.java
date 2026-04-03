package com.maksim.problemService.dto.contest;

import com.maksim.problemService.dto.problem.ProblemSignatureResponseDto;
import lombok.Data;

import java.time.Instant;
import java.util.List;


@Data
public class ContestResponseDto {
    private Integer id;
    private String title;
    private Integer authorId;
    private Instant startTime;
    private Instant endTime;
    private List<ProblemSignatureResponseDto> problems;
}
