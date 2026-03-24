package com.maksim.problemService.exception;

public class UnauthorizedAccessException extends RuntimeException{
    public UnauthorizedAccessException(String msg) {
        super(msg);
    }
}
