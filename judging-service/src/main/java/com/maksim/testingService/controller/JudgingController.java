package com.maksim.testingService.controller;

import com.maksim.testingService.dto.SaveTestCasesDto;
import com.maksim.testingService.exception.JuryCompilationException;
import com.maksim.testingService.service.JudgingService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
public class JudgingController {

    private final JudgingService testService;

    public JudgingController(JudgingService testService){
        this.testService = testService;
    }

    // используется только contest-problem-service во внутренней сети
    @PostMapping("/append-tests")
    public ResponseEntity<Object> appendTests(@RequestBody SaveTestCasesDto dto) {
        try {
            testService.saveTests(dto);
        } catch (JuryCompilationException ex){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ErrorMessage(ex.getMessage()));
        } catch (Exception ex){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
        return ResponseEntity.ok().build();
    }
}
