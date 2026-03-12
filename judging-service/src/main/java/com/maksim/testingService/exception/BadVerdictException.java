package com.maksim.testingService.exception;

public class BadVerdictException extends RuntimeException {
    public BadVerdictException(String msg) {
        super(msg);
    }
}
