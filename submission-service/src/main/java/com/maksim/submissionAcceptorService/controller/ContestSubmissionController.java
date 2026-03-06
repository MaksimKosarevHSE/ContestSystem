package com.maksim.submissionAcceptorService.controller;

import com.maksim.submissionAcceptorService.dto.SubmissionRequestDto;
import com.maksim.submissionAcceptorService.service.SubmissionService;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.concurrent.ExecutionException;

@Controller
public class ContestSubmissionController {
    private final SubmissionService submissionService;

    ContestSubmissionController(SubmissionService submitSolutionService) {
        this.submissionService = submitSolutionService;
    }

    @PostMapping("api/contest/{contestId}/problem/{problemId}/submit")
    public ResponseEntity<Object> submitSolution(@PathVariable Integer contestId, @PathVariable Integer problemId, @ModelAttribute SubmissionRequestDto solution) throws IOException, ExecutionException, InterruptedException {
        int userId = 1;
        long id = submissionService.submitSolution(problemId, contestId, userId, solution);
        return ResponseEntity.ok(id);
    }


    @GetMapping("api/contest/{contestId}/problem/{problemId}/submissions/my")
    public ResponseEntity<Object> getUserSubmissions(@PathVariable Integer contestId, @PathVariable Integer problemId,  @RequestParam(defaultValue = "1") Integer page) {
        int userId = 1;
        var result = submissionService.getSubmissions(problemId, contestId, userId, page);
        return ResponseEntity.ok(result);
    }


    @GetMapping("api/contest/{contestId}/submissions/my")
    public ResponseEntity<Object> getAllUserSubmissions(@PathVariable Integer contestId, @RequestParam(defaultValue = "1") Integer page) {
        int userId = 1;
        var result = submissionService.getAllUserPracticeSubmissions(userId, contestId, page);
        return ResponseEntity.ok(result);
    }

//    @GetMapping("api/contest/{contestId}/standings")
//    public ResponseEntity<Object> getSubmissions(@PathVariable Integer contestId, @RequestParam(defaultValue = "1") Integer page) {
//        var result = submissionService.getSuccessPracticeSubmissions(problemId, page);
//        return ResponseEntity.ok(result);
//    }

    @GetMapping("api/contest/submission/{submissionId}/details")
    public ResponseEntity<Object> getSubmissionDetails(@PathVariable Long submissionId) {
        int userId = 1;
        var result = submissionService.getSubmissionDetails(submissionId, userId);
        return ResponseEntity.ok(result);
    }

}

