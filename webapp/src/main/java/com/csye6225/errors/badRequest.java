package com.csye6225.errors;

public class badRequest extends RuntimeException {
    public badRequest(String message) {
        super(message);
    }
}

