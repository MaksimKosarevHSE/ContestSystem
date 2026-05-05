package com.maksim.problemService.controller;

import com.maksim.common.dto.PageResponseDto;
import com.maksim.problemService.dto.standings.UserProgressResponseDto;
import com.maksim.common.dto.ErrorResponse;
import com.maksim.problemService.service.StandingsService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin
@RequestMapping("/api")
@RequiredArgsConstructor
public class StandingsController {

    private final StandingsService standingsService;

    @GetMapping("/contest/{contestId}/standings")
    @Operation(summary = "Get contest's standings")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Leaderboard with details",
                    content = @Content(schema = @Schema(implementation = PageResponseDto.class))),
            @ApiResponse(responseCode = "404",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "500",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<PageResponseDto<UserProgressResponseDto>> getStandingsPage(@PathVariable Integer contestId,
                                                                                     @RequestParam(name = "page", defaultValue = "1") Integer page,
                                                                                     @RequestParam(defaultValue = "10") Integer pageSize) {
        return ResponseEntity.ok(standingsService.getLeaderboard(contestId, page, pageSize));
    }

    @GetMapping("/contest/{contestId}/user/{userId}/standings")
    @Operation(summary = "Get user's standings in the contest")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User's standings in the contest",
                    content = @Content(schema = @Schema(implementation = UserProgressResponseDto.class))),
            @ApiResponse(responseCode = "404",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "500",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<UserProgressResponseDto> getUserStandings(@PathVariable Integer contestId,
                                                                    @PathVariable Integer userId) {
        return ResponseEntity.ok(standingsService.getUserStandings(contestId, userId));
    }
}
