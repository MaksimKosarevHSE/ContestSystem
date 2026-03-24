package com.maksim.problemService.dto.problem;


import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@Builder
@Schema(description = "Сигнатура задачи")
public record ProblemSignatureResponseDto(
        Integer id,
        String title,
        Integer complexity
) {
}
