package com.maksim.problemService.controller;

import com.maksim.problemService.service.StandingsService;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class StandingsController {
    private final StandingsService standingsService;

    private final int PAGE_SIZE = 10;

    public StandingsController(StandingsService standingsService) {
        this.standingsService = standingsService;
    }

    @GetMapping("/api/contest/{contestId}/standings")
    public ResponseEntity<Object> getStandings(@PathVariable Integer contestId, @RequestParam(name = "page", defaultValue = "0") Integer page) {
        var result = standingsService.getLeaderboard(contestId, page, PAGE_SIZE);
        return ResponseEntity.ok(result);
    }


//    @GetMapping("/api/contest/{contestId}/user/{userId}/progress")
//    public ResponseEntity<Object> getUserProgress(@PathVariable Integer contestId, @PathVariable Integer userId){
//        var result = standingsService.getProgress(contestId, userId);
//    }
}
