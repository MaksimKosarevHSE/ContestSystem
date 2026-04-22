package com.maksim.problemService.controller;


import com.maksim.problemService.dto.PageResponseDto;
import com.maksim.problemService.dto.problem.ProblemConstrainsResponseDto;
import com.maksim.problemService.dto.problem.ProblemCreateDto;
import com.maksim.problemService.dto.problem.ProblemResponseDto;
import com.maksim.problemService.dto.problem.ProblemSignatureResponseDto;
import com.maksim.problemService.dto.problem.ProblemUpdateDto;
import com.maksim.problemService.entity.Problem;
import com.maksim.problemService.handler.ErrorResponse;
import com.maksim.problemService.service.ProblemService;
import com.maksim.problemService.service.ProblemServiceImpl;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin
@RequestMapping("/api")
@RequiredArgsConstructor
public class ProblemSetController {

    private final ProblemService problemService;

    private final Integer PAGE_SIZE = 20;

    @PostMapping(value = "/problem", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "Create new problem")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Problem created",
                    content = @Content(schema = @Schema(implementation = ProblemResponseDto.class))),
            @ApiResponse(responseCode = "401",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "400",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "500",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<ProblemResponseDto> createProblem(@Valid @ModelAttribute ProblemCreateDto problemCreateDto,
                                                            @RequestHeader(value = "X-User-Id") Integer userId) {
        return ResponseEntity.status(HttpStatus.CREATED).body(problemService.createProblem(problemCreateDto, userId));
    }


    @GetMapping("/problem/{id}")
    @Operation(summary = "Get problem by ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Problem description",
                    content = @Content(schema = @Schema(implementation = Problem.class))),
            @ApiResponse(responseCode = "404",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "500",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<ProblemResponseDto> getProblemById(@PathVariable Integer id) {
        return ResponseEntity.ok(problemService.getPublicProblemById(id));
    }


    @GetMapping("/problems")
    @Operation(summary = "Get problems signatures")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Problems' signatures",
                    content = @Content(schema = @Schema(implementation = PageResponseDto.class))),
            @ApiResponse(responseCode = "404",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "500",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<PageResponseDto<ProblemSignatureResponseDto>> getProblemsSignatures(@RequestParam(defaultValue = "1") Integer num) {
        return ResponseEntity.ok(problemService.getPublicProblemsSignatures(num, PAGE_SIZE));
    }


    @GetMapping("/problems/my")
    @Operation(summary = "Get users's problems signatures")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Problems' signatures",
                    content = @Content(schema = @Schema(implementation = PageResponseDto.class))),
            @ApiResponse(responseCode = "404",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "500",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<PageResponseDto<ProblemSignatureResponseDto>> getProblemsSignatures(@RequestParam(defaultValue = "1") Integer page,
                                                                                              @RequestHeader(value = "X-User-Id") Integer userId) {
        return ResponseEntity.ok(problemService.getUsersProblemsSignatures(userId, page, PAGE_SIZE));
    }


    @PutMapping("/problem/{id}")
    @Operation(summary = "Update problem")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Problem updated",
                    content = @Content(schema = @Schema(implementation = ProblemResponseDto.class))),
            @ApiResponse(responseCode = "404",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "500",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<ProblemResponseDto> updateProblem(
            @PathVariable Integer id,
            @Valid @RequestBody ProblemUpdateDto dto,
            @RequestHeader("X-User-Id") Integer userId) {
        return ResponseEntity.ok(problemService.updateProblem(id, dto, userId));
    }

    @GetMapping("/problem/my/{problemId}")
    @Operation(summary = "Get users' problem")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Problem description",
                    content = @Content(schema = @Schema(implementation = ProblemResponseDto.class))),
            @ApiResponse(responseCode = "404",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "500",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<ProblemResponseDto> getUsersProblem(@RequestHeader(value = "X-User-Id") Integer userId,
                                                              @PathVariable Integer problemId) {
        return ResponseEntity.ok(problemService.getUsersProblem(userId, problemId));
    }


    @DeleteMapping("/problem/{id}")
    @Operation(summary = "Delete problem")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Problem deleted",
                    content = @Content(schema = @Schema())),
            @ApiResponse(responseCode = "404",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "500",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<Void> deleteProblem(@PathVariable Integer id,
                                              @RequestHeader("X-User-Id") Integer userId) {
        problemService.deleteProblem(id, userId);
        return ResponseEntity.noContent().build();
    }


    @GetMapping("/problem/{problemId}/constraints")
    @Operation(summary = "Get problem's constraints")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Problem's constraints",
                    content = @Content(schema = @Schema(implementation = ProblemConstrainsResponseDto.class))),
            @ApiResponse(responseCode = "404",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "500",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })

    public ResponseEntity<ProblemConstrainsResponseDto> getProblemConstraints(@PathVariable Integer problemId) {
        return ResponseEntity.ok(problemService.getProblemConstraints(null, problemId));
    }
}
