package com.csye6225.errors;

public class IllegalChangeError extends RuntimeException {
    public IllegalChangeError(String message) {
        super(message);
    }
}
