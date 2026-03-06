package com.maksim.problemService.controller;

import com.maksim.problemService.dto.contest.ContestSignatureDto;
import com.maksim.problemService.dto.contest.CreateContestDto;
import com.maksim.problemService.dto.problem.ProblemSignature;
import com.maksim.problemService.entity.Problem;
import com.maksim.problemService.entity.ProblemConstraints;
import com.maksim.problemService.service.ContestService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@CrossOrigin
public class ContestController {
    private final ContestService contestService;

    private final Integer PAGE_SIZE = 20;

    public ContestController(ContestService contestService) {
        this.contestService = contestService;
    }

    // сигнатуры задач TESTED
    @GetMapping("/contest/{contestId}/problems")
    public ResponseEntity<Object> getSignatures(@PathVariable Integer contestId) {
        List<ProblemSignature> constraints = contestService.getAllProblemSignatures(contestId);
        return ResponseEntity.ok(constraints);
    }

    // полное описание заадачи TESTED
    @GetMapping("/contest/{contestId}/problem/{problemId}")
    public ResponseEntity<Object> getProblemById(@PathVariable Integer contestId, @PathVariable Integer problemId) {
        Problem problem = contestService.getProblem(contestId, problemId);
        return ResponseEntity.ok(problem);
    }

    //все публичные контесты TESTED
    @GetMapping("/contests")
    public ResponseEntity<Object> getAllPublicContests(@RequestParam(defaultValue = "1") Integer page) {
        List<ContestSignatureDto> list = contestService.getPublicContests(page, PAGE_SIZE);
        return ResponseEntity.ok(list);
    }

    // все котесты в которых участвовал / зарегестрирован / участвует текущий пользователь
    @GetMapping("/contests/my")
    public ResponseEntity<Object> getUserContests(@RequestParam(defaultValue = "1") Integer page) {
        int userId = 1;
        List<ContestSignatureDto> list = contestService.getUserContests(userId, page, PAGE_SIZE);
        return ResponseEntity.ok(list);
    }


    // TESTED
    @GetMapping("contest/{contestId}/problem/{problemId}/constraints")
    public ResponseEntity<Object> getConstraintsById(@PathVariable Integer contestId, @PathVariable Integer problemId) {
        ProblemConstraints constraints = contestService.getConstraints(contestId, problemId);
        return ResponseEntity.ok(constraints);
    }




    //TODO: дописать
    @PostMapping("/contest/create")
    public ResponseEntity<Object> createContest(@ModelAttribute CreateContestDto dto) {
        int userId = 1;
        int id = contestService.createContest(dto, userId);
        return ResponseEntity.ok(id);
    }

}

