package com.maksim.submissionAcceptorService.controller;

import com.maksim.submissionAcceptorService.SubmitSolutionDto;
import com.maksim.submissionAcceptorService.service.SubmitSolutionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;



@RestController
public class SubmitSolutionController {
    @Autowired
    private SubmitSolutionService submitSolutionService;

    @PostMapping("/submit")
    public ResponseEntity<String> submitSolution(@RequestBody SubmitSolutionDto solution){
        int id = submitSolutionService.createSubmission(solution,1);

        return ResponseEntity.status(HttpStatus.OK).body("Success");
    }
}
