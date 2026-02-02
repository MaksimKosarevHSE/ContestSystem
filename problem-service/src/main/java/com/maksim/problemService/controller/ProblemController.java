package com.maksim.problemService.controller;


import com.maksim.problemService.dto.ErrorMessage;
import com.maksim.problemService.dto.ProblemCreateDto;
import com.maksim.problemService.dto.validators.NotValidDtoException;
import com.maksim.problemService.entity.Problem;
import com.maksim.problemService.entity.ProblemConstraints;
import com.maksim.problemService.service.ProblemService;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
public class ProblemController {
    private ProblemService problemService;
    private int PAGE_SIZE = 20;

    public ProblemController(ProblemService service) {
        this.problemService = service;
    }

    @GetMapping("/problem/{id}")
    public ResponseEntity<Object> getProblemById(@PathVariable Integer id){
        var opt = problemService.findById(id);
        if (!opt.isPresent()){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ErrorMessage("No problem found with id " + id));
        }
        return ResponseEntity.ok(opt.get());
    }

    @GetMapping("/problem/{id}/constraints")
    public ResponseEntity<Object> getConstraintsById(@PathVariable Integer id){
        ProblemConstraints constraints = problemService.getConstraints(id);
        if (constraints == null){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ErrorMessage("No user found with id " + id));
        }
        return ResponseEntity.ok(constraints);
    }

    @GetMapping("/problem/page/{num}")
    public ResponseEntity<Object> getProblemsPage(@PathVariable Integer num){
        var page = problemService.getProblemsPage(num, PAGE_SIZE);
        return ResponseEntity.ok(page);
    }

    @GetMapping("/problem/signature/page/{num}")
    public ResponseEntity<Object> getProblemsSignatures(@PathVariable Integer num){
        var page = problemService.getProblemsSignaturesPage(num, PAGE_SIZE);
        return ResponseEntity.ok(page);
    }

    @PostMapping(value ="/problem", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Object> createProblem(@ModelAttribute ProblemCreateDto problemCreateDto){
        int userId = 1;
        try {
            Problem problem = problemService.createProblem(problemCreateDto, userId);
            return ResponseEntity.ok(problem);
        } catch (Exception ex){
            return ResponseEntity.badRequest().body(new ErrorMessage(ex.getMessage()));
        }
    }

    @PostMapping("/test")
    public ResponseEntity<Object> test(@RequestParam List<MultipartFile> file){
        System.out.println("NICE");
        return ResponseEntity.ok().build();
    }




}
