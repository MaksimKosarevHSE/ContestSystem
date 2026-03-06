package com.maksim.problemService.validators;


public class NotValidDtoException extends RuntimeException {
    public NotValidDtoException(String message) {
        super(message);
    }
}
