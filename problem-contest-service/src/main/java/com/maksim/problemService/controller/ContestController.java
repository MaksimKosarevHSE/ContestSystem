package com.maksim.problemService.controller;

import com.maksim.problemService.dto.PageResponseDto;
import com.maksim.problemService.dto.contest.ContestResponseDto;
import com.maksim.problemService.dto.contest.CreateContestDto;
import com.maksim.problemService.dto.contest.UpdateContestDto;
import com.maksim.problemService.dto.problem.ProblemResponseDto;
import com.maksim.problemService.handler.ErrorResponse;
import com.maksim.problemService.service.ContestService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@CrossOrigin
@RestController
@RequestMapping("/api")
public class ContestController {

    private final ContestService contestService;

    private final Integer PAGE_SIZE = 20;

    @PostMapping("/contest")
    @Operation(summary = "Create contest")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Contest created",
                    content = @Content(schema = @Schema(implementation = ContestResponseDto.class))),
            @ApiResponse(responseCode = "401",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "400",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "500",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<ContestResponseDto> createContest(@Valid @RequestBody CreateContestDto dto,
                                                            @RequestHeader(value = "X-User-Id") Integer userId) {
        return ResponseEntity.status(HttpStatus.CREATED).body(contestService.createContest(dto, userId));
    }

    @PatchMapping("/contest/{contestId}")
    @Operation(summary = "Update contest")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Contest updated",
                    content = @Content(schema = @Schema(implementation = ContestResponseDto.class))),
            @ApiResponse(responseCode = "401",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "400",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "500",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<ContestResponseDto> updateContest(@PathVariable Integer contestId,
                                                            @Valid @RequestBody UpdateContestDto dto,
                                                            @RequestHeader("X-User-Id") Integer userId) {
        return ResponseEntity.ok(contestService.updateContest(contestId, dto, userId));
    }

    @DeleteMapping("/contest/{contestId}")
    @Operation(summary = "Delete contest")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Contest deleted"),
            @ApiResponse(responseCode = "401",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "400",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "500",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<Void> deleteContest(@PathVariable Integer contestId,
                                              @RequestHeader("X-User-Id") Integer userId) {
        contestService.deleteContest(contestId, userId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/contest/{contestId}")
    @Operation(summary = "Get contest details")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Contest description",
                    content = @Content(schema = @Schema(implementation = ContestResponseDto.class))),
            @ApiResponse(responseCode = "401",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "400",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "500",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<ContestResponseDto> getContest(@PathVariable Integer contestId) {
        return ResponseEntity.ok(contestService.getContestById(contestId));
    }

    @GetMapping("/contests")
    @Operation(summary = "Get contests")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Contests with description",
                    content = @Content(schema = @Schema(implementation = ContestResponseDto.class))),
            @ApiResponse(responseCode = "404",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "500",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<PageResponseDto<ContestResponseDto>> getPublicContests(@RequestParam(defaultValue = "1") Integer page) {
        return ResponseEntity.ok(contestService.getPublicContests(page, PAGE_SIZE));
    }


    @GetMapping("/contests/participation/{userId}")
    @Operation(summary = "Get user's contests")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Contests with description",
                    content = @Content(schema = @Schema(implementation = ContestResponseDto.class))),
            @ApiResponse(responseCode = "404",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "500",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<PageResponseDto<ContestResponseDto>> getUserContests(@PathVariable Integer userId,
                                                                               @RequestParam(defaultValue = "1") Integer page) {
        return ResponseEntity.ok(contestService.getUsersContests(userId, page, PAGE_SIZE));
    }


    @PostMapping("/contest/{contestId}/contestants")
    @Operation(summary = "Register for contest")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Success registration"),
            @ApiResponse(responseCode = "400",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "401",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "500",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<Void> registerOnContest(@PathVariable Integer contestId,
                                                  @RequestHeader(value = "X-User-Id") Integer userId) {
        contestService.registerUser(contestId, userId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/contest/{contestId}/contestants")
    @Operation(summary = "Get registered users of contest")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "List of users' ID",
                    content = @Content(schema = @Schema(implementation = List.class))),
            @ApiResponse(responseCode = "404",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "500",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<PageResponseDto<Integer>> getContestants(@PathVariable Integer contestId,
                                                                   @RequestParam(defaultValue = "1") Integer page) {
        return ResponseEntity.ok(contestService.getRegisteredUsersIds(contestId, page, PAGE_SIZE));
    }


    @GetMapping("/contest/{contestId}/problem/{problemId}")
    @Operation(summary = "Get problem description in contest")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Problem description in contest",
                    content = @Content(schema = @Schema(implementation = ProblemResponseDto.class))),
            @ApiResponse(responseCode = "404",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "500",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })

    public ResponseEntity<ProblemResponseDto> getContestsProblem(@PathVariable Integer contestId,
                                                                 @PathVariable Integer problemId) {
        return ResponseEntity.ok(contestService.getProblem(contestId, problemId));
    }

}

