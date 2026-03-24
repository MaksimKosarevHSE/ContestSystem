package com.maksim.problemService.controller;

import com.maksim.problemService.dto.contest.ContestSignatureResponseDto;
import com.maksim.problemService.dto.contest.CreateContestDto;
import com.maksim.problemService.dto.problem.ProblemSignatureResponseDto;
import com.maksim.problemService.entity.Problem;
import com.maksim.problemService.exception.ErrorResponse;
import com.maksim.problemService.service.ContestService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
//@CrossOrigin
@RequestMapping("/api")
public class ContestController {
    private final ContestService contestService;

    private final Integer PAGE_SIZE = 20;

    public ContestController(ContestService contestService) {
        this.contestService = contestService;
    }

    @PostMapping("/contest/{contestId}/contestants")
    @Operation(summary = "Register on contest")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Success"),
            @ApiResponse(responseCode = "400",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "401",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "500",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<?> registerOnContest(@PathVariable Integer contestId,
                                               @RequestHeader(value = "X-User-Id", required = false) Integer userId) {
        if (userId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ErrorResponse("User is not authenticated"));
        }
        contestService.registerUser(contestId, userId);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/contest/{contestId}/contestants")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "List of contestants",
                    content = @Content(schema = @Schema(implementation = List.class))),
            @ApiResponse(responseCode = "404",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "500",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @Operation(summary = "Get contestants of contests")
    public ResponseEntity<?> getContestantsList(@PathVariable Integer contestId) {
        return ResponseEntity.ok(List.of("Vasya", "Dima"));
    }




    // Защитить, если не Upsolving
    @GetMapping("/contest/{contestId}/problems")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "List of all problems signatures in contest",
                    content = @Content(schema = @Schema(implementation = ProblemSignatureResponseDto.class))),
            @ApiResponse(responseCode = "404",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "500",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @Operation(summary = "Get signatures of contest's problems")
    public ResponseEntity<Object> getSignatures(@PathVariable Integer contestId) {
        List<ProblemSignatureResponseDto> constraints = contestService.getAllProblemSignatures(contestId);
        return ResponseEntity.ok(constraints);
    }

    // Защитить, если не Upsolving
    @GetMapping("/contest/{contestId}/problem/{problemId}")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Problem description in contest",
                    content = @Content(schema = @Schema(implementation = Problem.class))),
            @ApiResponse(responseCode = "404",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "500",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @Operation(summary = "Get full problem description")
    public ResponseEntity<Object> getProblemById(@PathVariable Integer contestId, @PathVariable Integer problemId) {
        Problem problem = contestService.getProblem(contestId, problemId);
        return ResponseEntity.ok(problem);
    }


    @GetMapping("/contests")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "List of contests",
                    content = @Content(schema = @Schema(implementation = ContestSignatureResponseDto.class))),
            @ApiResponse(responseCode = "404",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "500",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @Operation(summary = "Get list of contests")
    public ResponseEntity<Object> getAllPublicContests(@RequestParam(defaultValue = "1") Integer page, HttpServletRequest req) {
        Page<ContestSignatureResponseDto> pageResult = contestService.getPublicContests(page, PAGE_SIZE);
        return ResponseEntity.ok(pageResult);
    }

    @GetMapping("/contests/participation/{userId}")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "List of contests in which user have participated or registered",
                    content = @Content(schema = @Schema(implementation = ContestSignatureResponseDto.class))),
            @ApiResponse(responseCode = "404",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "500",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @Operation(summary = "Get user's history of participation in contests")
    public ResponseEntity<Object> getUserContests(@PathVariable Integer userId,
                                                  @RequestParam(defaultValue = "1") Integer page) {
        Page<ContestSignatureResponseDto> list = contestService.getUserContests(userId, page, PAGE_SIZE);
        return ResponseEntity.ok(list);
    }

    // Защитить, если не Upsolving
    @GetMapping("contest/{contestId}/problem/{problemId}/constraints")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Constraints of problem in contest",
                    content = @Content(schema = @Schema(implementation = ProblemConstraintsResponseDto.class))),
            @ApiResponse(responseCode = "404",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "500",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @Operation(summary = "Get problem's runtime limit, memory limit, compile time limit, contest info")
    public ResponseEntity<Object> getConstraintsById(@PathVariable Integer contestId, @PathVariable Integer problemId) {
        ProblemConstraintsResponseDto constraints = contestService.getConstraints(contestId, problemId);
        return ResponseEntity.ok(constraints);
    }


    @PostMapping("/contest")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Contest's id",
                    content = @Content(schema = @Schema(implementation = Long.class))),
            @ApiResponse(responseCode = "401",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "400",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "500",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @Operation(summary = "Create new contest")
    public ResponseEntity<Object> createContest(@Valid @RequestBody CreateContestDto dto,
                                                @RequestHeader(value = "X-User-Id", required = false) Integer userId) {
        if (userId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ErrorResponse("User is not authenticated"));
        }
        int id = contestService.createContest(dto, userId);
        return ResponseEntity.ok(id);
    }

}

