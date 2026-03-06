package com.maksim.problemService.dto.contest;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class ContestSignatureDto {
    private int id;
    private int authorId;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
}
