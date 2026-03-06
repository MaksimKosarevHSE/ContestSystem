package com.maksim.testingService.controller;

import com.maksim.testingService.DTO.ErrorMessage;
import com.maksim.testingService.DTO.SaveTestsDto;
import com.maksim.testingService.entity.CheckerType;
import com.maksim.testingService.enums.ProgrammingLanguage;
import com.maksim.testingService.exceptions.JuryCompilationException;
import com.maksim.testingService.service.TestService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import java.io.IOException;

@RestController
@Slf4j
public class TestsController {

    private TestService testService;

    public TestsController(TestService testService){
        this.testService = testService;
    }

    @PostMapping("/append-tests")
    public ResponseEntity<Object> appendTests(@RequestBody SaveTestsDto dto) {
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
