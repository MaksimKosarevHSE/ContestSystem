package com.maksim.problemService.dto.contest;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;

import java.time.Instant;
import java.util.List;


@Schema(description = "Данные для создания нового контеста")
public record CreateContestDto(
        @Schema(description = "Название контеста", example = "Кубок Трёх Флешек")
        @NotBlank(message = "Название контеста обязательно")
        @Size(max = 255, message = "Название не может превышать 255 символов")
        String title,

        @Schema(description = "Публичный ли контест", example = "true")
        Boolean isPublic,

        @Schema(description = "Время начала", example = "2027-04-01T10:00:00Z")
        @NotNull(message = "Время начала обязательно")
        @Future(message = "Время начала должно быть в будущем")
        Instant startTime,

        @Schema(description = "Время окончания", example = "2028-04-01T12:00:00Z")
        @NotNull(message = "Время окончания обязательно")
        @Future(message = "Время окончания должно быть в будущем")
        Instant endTime,

        @Schema(description = "Список ID задач, включённых в контест")
        @NotEmpty(message = "Список задач не может быть пустым")
        List<@Positive Integer> problemsId
) {
}
