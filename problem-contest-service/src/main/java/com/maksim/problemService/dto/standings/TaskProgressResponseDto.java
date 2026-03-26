package com.maksim.problemService.dto.standings;


public record TaskProgressResponseDto(
        Integer taskId,
        Boolean solved,
        Integer attempts,
        Integer secondsAfterSolving,
        Integer score
) {
}
