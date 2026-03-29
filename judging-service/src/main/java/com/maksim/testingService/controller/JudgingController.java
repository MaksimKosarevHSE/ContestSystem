package com.maksim.testingService.controller;

import com.maksim.testingService.dto.SaveTestCasesRequestDto;
import com.maksim.testingService.exception.JuryCompilationException;
import com.maksim.testingService.service.JudgingService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springdoc.api.ErrorMessage;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
public class JudgingController {

    private final JudgingService judgingService;

    // 1 эндпоинт использует только contest-problem-service во внутренней сети, поэтому обработка сделана максимально просто

    @PostMapping("/append-tests")
    public ResponseEntity<?> appendTests(@RequestBody SaveTestCasesRequestDto dto) {
        try {
            judgingService.saveTests(dto);
        } catch (JuryCompilationException ex) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ErrorMessage(ex.getMessage()));
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
        return ResponseEntity.ok().build();
    }
}