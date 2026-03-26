package com.maksim.problemService.dto.contest;

import jakarta.validation.constraints.*;

import java.time.LocalDateTime;
import java.util.List;

public record UpdateContestDto(
        @Size(max = 255)
        String title,

        @Future
        LocalDateTime startTime,

        @Future
        LocalDateTime endTime,

        Boolean isPublic,

        List<@Positive Integer> problemsId
) {
}