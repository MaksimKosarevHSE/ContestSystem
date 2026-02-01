package com.maksim.problemService.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.cglib.core.Local;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ErrorMessage {
    private LocalDateTime time;
    private String message;
    public ErrorMessage(String message){
        this.time = LocalDateTime.now();
        this.message = message;
    }
}
