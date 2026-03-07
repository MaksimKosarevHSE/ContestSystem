package com.maksim.auth_service.service;

public class AuthException extends RuntimeException {
    public AuthException(String message) {
        super(message);
    }
}
