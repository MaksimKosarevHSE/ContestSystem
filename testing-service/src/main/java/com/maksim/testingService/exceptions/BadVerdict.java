package com.maksim.testingService.exceptions;

public class BadVerdict extends RuntimeException {
    public BadVerdict(String msg) {
        super(msg);
    }
}
