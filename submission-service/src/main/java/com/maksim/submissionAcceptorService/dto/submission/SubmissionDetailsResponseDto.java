package com.maksim.submissionAcceptorService.dto.submission;

import com.maksim.submissionAcceptorService.enums.ProgrammingLanguage;
import com.maksim.submissionAcceptorService.enums.Status;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.Instant;
import java.time.LocalDateTime;
@Data
@Schema(description = "Full submission information")
public class SubmissionDetailsResponseDto{
        @Schema(description = "Submission ID", example = "12345")
        private Long id;

        @Schema(description = "Sender's ID", example = "42")
        private Integer userId;

        @Schema(description = "Problem ID", example = "101")
        private Integer problemId;

        @Schema(description = "The contest ID where the solution was submitted")
        private Integer contestId;

        @Schema(description = "Is upsolving")
        private Boolean isUpsolving;

        @Schema(description = "Submission time", example = "2026-01-01T00:00:00Z")
        private Instant time;

        @Schema(description = "Submission source code", example = "print('Hello world!')")
        private String source;

        @Schema(description = "Programming language", example = "CPP")
        private ProgrammingLanguage programmingLanguage;

        @Schema(description = "Verdict", example = "OK")
        private Status status;

        @Schema(description = "Execution time (ms)", example = "150")
        private Integer executionTime;

        @Schema(description = "Used memory (KB)", example = "10240")
        private Integer usedMemory;

        @Schema(description = "The last test on which the solution was tested", example = "3")
        private Integer testNum;

        @Schema(description = "Checker's message", example = "Expected: 6, provided: 7 in row 5")
        private String checkerMessage;
}