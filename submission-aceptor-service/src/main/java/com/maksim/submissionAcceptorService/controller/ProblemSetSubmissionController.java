package com.maksim.submissionAcceptorService.controller;

import com.maksim.submissionAcceptorService.dto.ErrorMessage;
import com.maksim.submissionAcceptorService.dto.SubmissionRequestDto;
import com.maksim.submissionAcceptorService.dto.SubmitSolutionTextDto;
import com.maksim.submissionAcceptorService.service.SubmissionService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.concurrent.ExecutionException;


@RestController
@CrossOrigin
@Slf4j

public class ProblemSetSubmissionController {
    private SubmissionService submissionService;

    ProblemSetSubmissionController(SubmissionService submitSolutionService){
        this.submissionService = submitSolutionService;
    }

    @PostMapping("api/problemset/{problemId}/submit")
    public ResponseEntity<Object> submitSolution(@PathVariable Integer problemId, @ModelAttribute SubmissionRequestDto solution) throws IOException, ExecutionException, InterruptedException {
        int userId = 1;
        long id = submissionService.submitPracticeSolution(problemId,userId, solution);
        return ResponseEntity.ok(id);
    }

    @GetMapping("api/problemset/{problemId}/sumbissions/my")
    public ResponseEntity<Object> getMySubmissions(@PathVariable Integer problemId, @RequestParam(defaultValue = "1") Integer page){
        int userId = 1;
        var result = submissionService.getUserSubmissions(problemId, userId, page);
        return ResponseEntity.ok(result);
    }

    @GetMapping("api/problemset/{problemId}/all-success-submissions")
    public ResponseEntity<Object> getSubmissions(@PathVariable Integer problemId, @RequestParam(defaultValue = "1") Integer page){
        var result = submissionService.getSuccessSubmissions(problemId, page);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Object> getSubmission(@PathVariable long id){
        var dto = submissionService.getSubmission(id);
        return ResponseEntity.ok(dto);
    }

    @GetMapping
    public ResponseEntity<Object> getAllSubmissions(){ // для тестов
        var list = submissionService.getAllSubmissions();
        return ResponseEntity.ok(list);
    }

}
