package com.maksim.submissionAcceptorService.dto.submission;

import com.maksim.common.enums.ProgrammingLanguage;
import com.maksim.common.enums.Status;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.time.Instant;
import java.time.LocalDateTime;

@Data
@Schema(description = "Short submission information")
public class SubmissionResponseDto {
    @Schema(description = "Submission ID", example = "12345")
    private Long id;

    @Schema(description = "Sender's ID", example = "42")
    private Integer userId;

    @Schema(description = "Problem ID", example = "101")
    private Integer problemId;

    @Schema(description = "Submission time", example = "2026-01-01T00:00:00Z")
    private Instant time;

    @Schema(description = "Programming language", example = "CPP")
    private ProgrammingLanguage programmingLanguage;

    @Schema(description = "Verdict", example = "OK")
    private Status status;

    @Schema(description = "The test number on which the current submission is being tested", example = "2")
    private Integer testNum;

    @Schema(description = "Execution time (ms)", example = "150")
    private Integer executionTime;

    @Schema(description = "Used memory (KB)", example = "10240")
    private Integer usedMemory;
}
