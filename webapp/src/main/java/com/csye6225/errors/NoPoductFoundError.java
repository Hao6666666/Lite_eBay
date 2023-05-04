package com.csye6225.errors;

public class NoPoductFoundError extends RuntimeException {
    public NoPoductFoundError(String message) {
        super(message);
    }
}
