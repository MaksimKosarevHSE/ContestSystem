package com.maksim.problemService.controller;

import com.maksim.problemService.dto.standings.UserProgressResponseDto;
import com.maksim.problemService.exception.ErrorResponse;
import com.maksim.problemService.service.StandingsService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@CrossOrigin
@RequestMapping("/api")
@RequiredArgsConstructor
public class StandingsController {

    private final StandingsService standingsService;

    private final int PAGE_SIZE = 10;

    @GetMapping("/contest/{contestId}/standings")
    @Operation(summary = "Get contest's standings")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Submission was accepted. Returns ID",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "500",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<List<UserProgressResponseDto>> getStandings(@PathVariable Integer contestId,
                                                                      @RequestParam(name = "page", defaultValue = "1") Integer page) {
        return ResponseEntity.ok(standingsService.getLeaderboard(contestId, page, PAGE_SIZE));
    }
}
