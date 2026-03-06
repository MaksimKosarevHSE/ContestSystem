package com.maksim.problemService.dto.contest;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
public class CreateContestDto {
    private String title;
    private boolean isPublic;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private List<Integer> problemsId;
}
