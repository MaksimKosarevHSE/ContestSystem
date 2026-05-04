package com.maksim.problemService.dto.contest;

import jakarta.validation.constraints.*;

import java.time.Instant;
import java.util.List;

public record UpdateContestDto(
        @Size(max = 255)
        String title,

        @Future
        Instant startTime,

        @Future
        Instant endTime,

        List<@Positive Integer> problemsId
) {
}
