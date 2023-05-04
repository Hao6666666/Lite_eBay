package com.csye6225.errors;

public class AuthenticationError extends RuntimeException {
    public AuthenticationError(String message){
        super(message);
    }
}
