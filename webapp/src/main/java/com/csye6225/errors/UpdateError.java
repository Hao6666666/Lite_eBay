package com.csye6225.errors;

public class UpdateError extends RuntimeException {
    public UpdateError(String message) {
        super(message);
    }
}
