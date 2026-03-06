package com.maksim.submissionAcceptorService.controller;
import com.maksim.submissionAcceptorService.dto.SubmissionRequestDto;
import com.maksim.submissionAcceptorService.service.SubmissionService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.io.IOException;
import java.util.concurrent.ExecutionException;


@RestController
@CrossOrigin
@Slf4j
public class ProblemSetSubmissionController {
    private final SubmissionService submissionService;

    ProblemSetSubmissionController(SubmissionService submitSolutionService) {
        this.submissionService = submitSolutionService;
    }

    @PostMapping("api/problemset/{problemId}/submit")
    public ResponseEntity<Object> submitSolution(@PathVariable Integer problemId, @ModelAttribute SubmissionRequestDto solution) throws IOException, ExecutionException, InterruptedException {
        int userId = 1;
        long id = submissionService.submitSolution(problemId, null, userId, solution);
        return ResponseEntity.ok(id);
    }

    @GetMapping("api/problemset/{problemId}/sumbissions/my")
    public ResponseEntity<Object> getUserSubmissions(@PathVariable Integer problemId, @RequestParam(defaultValue = "1") Integer page) {
        int userId = 1;
        var result = submissionService.getSubmissions(problemId, null, userId, page);
        return ResponseEntity.ok(result);
    }

    @GetMapping("api/problemset/sumbissions/my")
    public ResponseEntity<Object> getAllUserSubmissions(@RequestParam(defaultValue = "1") Integer page) {
        int userId = 1;
        var result = submissionService.getAllUserPracticeSubmissions(userId,null, page);
        return ResponseEntity.ok(result);
    }

    @GetMapping("api/problemset/{problemId}/standings")
    public ResponseEntity<Object> getSubmissions(@PathVariable Integer problemId, @RequestParam(defaultValue = "1") Integer page) {
        var result = submissionService.getSuccessPracticeSubmissions(problemId, page);
        return ResponseEntity.ok(result);
    }

    @GetMapping("api/problemset/submission/{submissionId}/details")
    public ResponseEntity<Object> getSubmissionDetails(@PathVariable Long submissionId) {
        int userId = 1;
        var result = submissionService.getSubmissionDetails(submissionId, userId);
        return ResponseEntity.ok(result);
    }

}
