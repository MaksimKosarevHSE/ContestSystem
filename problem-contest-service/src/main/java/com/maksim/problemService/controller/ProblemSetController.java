package com.maksim.problemService.controller;


import com.maksim.problemService.dto.problem.ProblemCreateDto;
import com.maksim.problemService.dto.problem.ProblemSignatureResponseDto;
import com.maksim.problemService.entity.Problem;
import com.maksim.problemService.dto.problem.ProblemConstraints;
import com.maksim.problemService.exception.ErrorResponse;
import com.maksim.problemService.service.ProblemService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin
@RequestMapping("/api")
public class ProblemSetController {
    private final ProblemService problemService;

    private final int PAGE_SIZE = 20;

    public ProblemSetController(ProblemService service) {
        this.problemService = service;
    }

    @PostMapping(value = "/problem", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Problem's id",
                    content = @Content(schema = @Schema(implementation = Long.class))),
            @ApiResponse(responseCode = "401",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "400",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "500",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @Operation(summary = "Create new problem")
    public ResponseEntity<Object> createProblem(@Valid @ModelAttribute ProblemCreateDto problemCreateDto,
                                                @RequestHeader(value = "X-User-Id", required = false) Integer userId) {
        if (userId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ErrorResponse("User is not authenticated"));
        }

        return ResponseEntity.ok(problemService.createProblem(problemCreateDto, userId));
    }


    @GetMapping("/problem/{id}")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Problem description",
                    content = @Content(schema = @Schema(implementation = Problem.class))),
            @ApiResponse(responseCode = "404",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "500",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @Operation(summary = "Get full problem description")
    public ResponseEntity<Object> getProblemById(@PathVariable Integer id) {
        return ResponseEntity.ok(problemService.findById(id));
    }


    @GetMapping("/problem/{id}/constraints")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Constraints of problem",
                    content = @Content(schema = @Schema(implementation = ProblemConstraints.class))),
            @ApiResponse(responseCode = "404",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "500",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @Operation(summary = "Get problem's runtime limit, memory limit, compile time limit, contest info")
    public ResponseEntity<Object> getConstraintsById(@PathVariable Integer id) {
        return ResponseEntity.ok(problemService.getConstraints(id));
    }

    @GetMapping("/problem/signature")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "List of problem's signatures",
                    content = @Content(schema = @Schema(implementation = ProblemSignatureResponseDto.class))),
            @ApiResponse(responseCode = "404",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "500",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @Operation(summary = "Get problem signature")
    public ResponseEntity<Object> getProblemsSignatures(@RequestParam(defaultValue = "1") Integer num) {
        var page = problemService.getProblemsSignaturesPage(num, PAGE_SIZE);
        return ResponseEntity.ok(page);
    }

}
