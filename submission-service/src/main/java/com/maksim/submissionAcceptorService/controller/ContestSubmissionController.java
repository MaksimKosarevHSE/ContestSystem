package com.maksim.submissionAcceptorService.controller;

import com.maksim.common.dto.PageResponseDto;
import com.maksim.submissionAcceptorService.dto.submission.SubmissionCreateDto;
import com.maksim.submissionAcceptorService.dto.submission.SubmissionDetailsResponseDto;
import com.maksim.submissionAcceptorService.dto.submission.SubmissionResponseDto;
import com.maksim.common.enums.Status;
import com.maksim.common.dto.ErrorResponse;
import com.maksim.submissionAcceptorService.service.SubmissionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/sub")
@RequiredArgsConstructor
@Tag(name = "Contest submissions", description = "Managing submissions within contests")
public class ContestSubmissionController {

    private final SubmissionService submissionService;

    @PostMapping(value = "/contest/{contestId}/problem/{problemId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "Submit contest's problem solution")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Submission is accepted",
                    content = @Content(schema = @Schema(implementation = SubmissionResponseDto.class))),
            @ApiResponse(responseCode = "400",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "401",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "500",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<SubmissionResponseDto> submitSolution(@PathVariable Integer contestId,
                                                                @PathVariable Integer problemId,
                                                                @ModelAttribute SubmissionCreateDto solution,
                                                                @RequestHeader(value = "X-User-Id") Integer userId
    ) {
        return ResponseEntity.status(HttpStatus.CREATED).body(submissionService.submitSolution(problemId, contestId, userId, solution));
    }

    @GetMapping("/contest/{contestId}/submission/{submissionId}")
    @Operation(summary = "Get contest submission by contest ID and submission ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Submission",
                    content = @Content(schema = @Schema(implementation = SubmissionResponseDto.class))),
            @ApiResponse(responseCode = "400",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "500",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<SubmissionResponseDto> getContestSubmission(@PathVariable Long submissionId,
                                                                      @PathVariable Integer contestId) {
        return ResponseEntity.ok(submissionService.getSubmission(submissionId, contestId));
    }

    @GetMapping("/contest/{contestId}/submissions")
    @Operation(summary = "Get contest submissions (with filters)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Page of submissions",
                    content = @Content(schema = @Schema(implementation = PageResponseDto.class))),
            @ApiResponse(responseCode = "400",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "500",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<PageResponseDto<SubmissionResponseDto>> getContestSubmissions(@PathVariable Integer contestId,
                                                                                        @RequestParam(required = false) Integer problemId,
                                                                                        @RequestParam(required = false) Integer userId,
                                                                                        @RequestParam(required = false) Status status,
                                                                                        @RequestParam(defaultValue = "1") Integer page) {
        return ResponseEntity.ok(submissionService.getSubmissions(contestId, problemId, userId, status, page));
    }

    @GetMapping("/contest/{contestId}/submission/{submissionId}/details")
    @Operation(summary = "Get submission details by contest ID and submission ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Submission details",
                    content = @Content(schema = @Schema(implementation = SubmissionDetailsResponseDto.class))),
            @ApiResponse(responseCode = "400",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "403",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "500",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<SubmissionDetailsResponseDto> getContestSubmissionDetails(@PathVariable Long submissionId,
                                                                                    @PathVariable Integer contestId,
                                                                                    @RequestHeader(value = "X-User-Id", required = false) Integer userId) {
        return ResponseEntity.ok(submissionService.getSubmissionDetails(submissionId, contestId, userId));
    }
}