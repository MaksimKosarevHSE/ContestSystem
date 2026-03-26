package com.maksim.problemService.dto.standings;


import java.util.List;

public record UserProgressResponseDto(
        Integer userId,
        Integer place,
        List<TaskProgressResponseDto> taskProgress,
        Integer score
) {
}
