package com.maksim.submissionAcceptorService.controller;

import com.maksim.submissionAcceptorService.dto.PageResponseDto;
import com.maksim.submissionAcceptorService.dto.submission.SubmissionCreateDto;
import com.maksim.submissionAcceptorService.dto.submission.SubmissionDetailsResponseDto;
import com.maksim.submissionAcceptorService.dto.submission.SubmissionResponseDto;
import com.maksim.submissionAcceptorService.enums.ProgrammingLanguage;
import com.maksim.submissionAcceptorService.enums.Status;
import com.maksim.submissionAcceptorService.handler.ErrorResponse;
import com.maksim.submissionAcceptorService.service.SubmissionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/sub")
@RequiredArgsConstructor
@Tag(name = "Contest Submissions", description = "Managing submissions within the contests")
public class ContestSubmissionController {

    private final SubmissionService submissionService;

    @PostMapping(value = "/contest/{contestId}/problem/{problemId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "Submit problem solution in contest")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Submission is accepted",
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

    @GetMapping("/contest/{contestId}/submissions")
    @Operation(summary = "Get problems submissions in contest (with filters)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Contest submissions",
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

    @GetMapping("/contest/{contestId}/submission/{submissionId}")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Contest submission details",
                    content = @Content(schema = @Schema(implementation = SubmissionDetailsResponseDto.class))),
            @ApiResponse(responseCode = "400",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "500",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<SubmissionResponseDto> getContestSubmission(@PathVariable Long submissionId,
                                                                      @PathVariable Integer contestId,
                                                                      @RequestHeader(value = "X-User-Id", required = false) Integer userId) {
        return ResponseEntity.ok(submissionService.getSubmission(submissionId, contestId));
    }

    @GetMapping("/contest/{contestId}/submission/{submissionId}/details")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Contest submission details",
                    content = @Content(schema = @Schema(implementation = SubmissionDetailsResponseDto.class))),
            @ApiResponse(responseCode = "400",
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

