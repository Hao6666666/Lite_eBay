package com.csye6225.errors;

import jakarta.validation.ConstraintViolationException;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ErrorHandler {

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity handleConstraintViolationException(ConstraintViolationException e) {
        return ResponseEntity.badRequest().body(e.getMessage());
    }

    @ExceptionHandler(EmailError.class)
    public ResponseEntity handleRepeatEmailException(EmailError e) {
        return ResponseEntity.badRequest().body(e.getMessage());
    }

    @ExceptionHandler(DuplicatedSku.class)
    public ResponseEntity handleRepeatEmailException(DuplicatedSku e) {
        return ResponseEntity.badRequest().body(e.getMessage());
    }

    @ExceptionHandler(IllegalChangeError.class)
    public ResponseEntity handleRepeatEmailException(IllegalChangeError e) {
        return ResponseEntity.status(403).body(e.getMessage());
    }

    @ExceptionHandler(UpdateError.class)
    public ResponseEntity handleInvalidUpdateException(UpdateError e) {
        return ResponseEntity.badRequest().body(e.getMessage());
    }

    @ExceptionHandler(NoPoductFoundError.class)
    public ResponseEntity handleInvalidUpdateException(NoPoductFoundError e) {
        return ResponseEntity.status(404).body(e.getMessage());
    }

    @ExceptionHandler(GetUserInfoError.class)
    public ResponseEntity handleInvalidUpdateException(GetUserInfoError e) {
        return ResponseEntity.status(403).body(e.getMessage());
    }

    @ExceptionHandler(badRequest.class)
    public ResponseEntity handleInvalidUpdateException(badRequest e) {
        return ResponseEntity.status(400).body(e.getMessage());
    }

    @ExceptionHandler(AuthenticationError.class)
    public ResponseEntity handleUnauthorizedException(AuthenticationError e){
        return ResponseEntity.status(401).body(e.getMessage());
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity handleHttpMessageNotReadableException(HttpMessageNotReadableException e){
        return ResponseEntity.badRequest().build();
    }
}

