package com.maksim.submissionAcceptorService.controller;

import com.maksim.submissionAcceptorService.dto.CreateSubmissionDto;
import com.maksim.submissionAcceptorService.dto.SubmissionResponseDto;
import com.maksim.submissionAcceptorService.enums.ProgrammingLanguage;
import com.maksim.submissionAcceptorService.enums.Status;
import com.maksim.submissionAcceptorService.exception.ErrorResponse;
import com.maksim.submissionAcceptorService.repository.SubmissionRepository;
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
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.concurrent.ExecutionException;

@Controller
@RequiredArgsConstructor
@RequestMapping("/api/sub")
@Tag(name = "Contest Submissions", description = "Managing submissions within the contests")
public class ContestSubmissionController {

    private final SubmissionService submissionService;

    @PostMapping("/contest/{contestId}/problem/{problemId}")
    @Operation(summary = "Submit solution on problem from contest")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Submission was accepted. Returns ID",
                    content = @Content(schema = @Schema(implementation = Long.class))),
            @ApiResponse(responseCode = "400",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "401",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "500",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<?> submitSolution(@PathVariable @Parameter(description = "Contest id") Integer contestId,
                                            @PathVariable @Parameter(description = "Problem id") Integer problemId,
                                            @ModelAttribute @Parameter(description = "Solution data") CreateSubmissionDto solution,
                                            @RequestHeader(value = "X-User-Id", required = false) @Parameter(description = "Service header") Integer userId
    ) {
        if (userId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ErrorResponse("User is not authenticated"));
        }
        long id = submissionService.submitSolution(problemId, contestId, userId, solution);
        return ResponseEntity.ok(id);
    }


    @GetMapping("/contest/{contestId}/submissions")
    @Operation(summary = "Get all submissions of problems in contest set (with filters)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Page response with short information about submissions",
                    content = @Content(schema = @Schema(implementation = SubmissionResponseDto.class))),
            @ApiResponse(responseCode = "400",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "500",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<?> getSubmissions(@PathVariable @Parameter(description = "Contest ID") Integer contestId,
                                            @RequestParam(required = false) @Parameter(description = "Problem ID", example = "10") Integer problemId,
                                            @RequestParam(required = false) @Parameter(description = "User ID", example = "101101") Integer userId,
                                            @RequestParam(required = false) @Parameter(description = "Submission status", example = "OK") Status status,
                                            @RequestParam(required = false) @Parameter(description = "Programming language", example = "CPP") ProgrammingLanguage language,
                                            @RequestParam(defaultValue = "1") @Parameter(description = "Page number", example = "1") Integer page) {

        var res = submissionService.getSubmissions(contestId, problemId, userId, status, language, page);
        return ResponseEntity.ok(res);
    }

    @GetMapping("/contest/{contestId}/submission/{submissionId}/details")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Details about submission in contest",
                    content = @Content(schema = @Schema(implementation = SubmissionResponseDto.class))),
            @ApiResponse(responseCode = "400",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "500",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<Object> getSubmissionDetails(@PathVariable @Parameter(description = "Submission ID") Long submissionId,
                                                       @PathVariable @Parameter(description = "Contest ID") Integer contestId,
                                                       @RequestHeader(value = "X-User-Id", required = false) @Parameter(description = "Service header") Integer userId) {
        if (userId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ErrorResponse("User is not authenticated"));
        }
        var result = submissionService.getSubmissionDetails(submissionId, contestId, userId);
        return ResponseEntity.ok(result);
    }

}

