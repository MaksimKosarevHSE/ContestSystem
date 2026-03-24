package com.maksim.problemService.dto.contest;

import jakarta.validation.constraints.*;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class UpdateContestDto {
    @Size(max = 255)
    private String title;

    @Future
    private LocalDateTime startTime;

    @Future
    private LocalDateTime endTime;

    private Boolean isPublic;

    @NotEmpty
    private List<@Positive Integer> problemsId;
}