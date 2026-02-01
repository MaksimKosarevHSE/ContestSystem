package com.maksim.problemService.dto.validators;


public class NotValidDtoException extends RuntimeException {
    public NotValidDtoException(String message) {
        super(message);
    }
}
