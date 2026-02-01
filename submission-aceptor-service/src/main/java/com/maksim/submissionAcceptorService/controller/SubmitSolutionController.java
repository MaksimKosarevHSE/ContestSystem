package com.maksim.submissionAcceptorService.controller;

import com.maksim.submissionAcceptorService.dto.ErrorMessage;
import com.maksim.submissionAcceptorService.dto.SubmitSolutionFileDto;
import com.maksim.submissionAcceptorService.dto.SubmitSolutionTextDto;
import com.maksim.submissionAcceptorService.service.SubmitSolutionService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.time.LocalDateTime;


@RestController
@RequestMapping("/submit")
@Slf4j
public class SubmitSolutionController {
    private SubmitSolutionService submitSolutionService;

    SubmitSolutionController(SubmitSolutionService submitSolutionService){
        this.submitSolutionService = submitSolutionService;
    }


    @PostMapping("/text")
    public ResponseEntity<Object> submitSolution(@RequestBody SubmitSolutionTextDto solution){
        return process(solution);
    }

    @PostMapping(value = "/file", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Object> submitSolution(@ModelAttribute SubmitSolutionFileDto solution) {
        SubmitSolutionTextDto convertedDto;
        try {
            String textSource = new String(solution.getSource().getBytes());
            convertedDto = new SubmitSolutionTextDto(solution.getProblemId(), textSource, solution.getLanguage());
        } catch (IOException ex){
            log.error("Can't source file {}", ex.getMessage());
            return ResponseEntity.badRequest().body(new ErrorMessage(LocalDateTime.now(), "File reading exception"));
        }
        return process(convertedDto);
    }

    private ResponseEntity<Object> process(SubmitSolutionTextDto solution){
        int userId = 1;
        long submissionId = -1;
        try {
            submissionId = submitSolutionService.createSubmission(solution,userId);
        } catch (Exception ex){
            log.error("Can't accept submission. {}", ex.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ErrorMessage(LocalDateTime.now(), ex.getMessage()));
        }
        return ResponseEntity.ok(submissionId);
    }
}
