package com.maksim.testingService.DTO;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ErrorMessage {
    private String message;
    private LocalDateTime time;
    public ErrorMessage(String message){
        this.message = message;
        this.time = LocalDateTime.now();
    }
}
