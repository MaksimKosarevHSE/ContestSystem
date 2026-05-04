package com.maksim.common.dto;

public record ErrorResponse(String message) {
    public static ErrorResponse of(String message) {
        return new ErrorResponse(message);
    }
}
