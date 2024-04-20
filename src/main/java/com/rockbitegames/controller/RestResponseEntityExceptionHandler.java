package com.rockbitegames.controller;

import com.rockbitegames.exception.KafkaMessageNotSentException;
import com.rockbitegames.exception.OptionalExceptionHandler;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@ControllerAdvice
public class RestResponseEntityExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(value = { KafkaMessageNotSentException.class})
    protected ResponseEntity<Object> kafkaConflictHandler(RuntimeException ex, WebRequest request) {
        String bodyOfResponse = String.format("Could not send message to kafka via producer: %s.", ex.getMessage());
        return new ResponseEntity<>(bodyOfResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(value = { OptionalExceptionHandler.class})
    protected ResponseEntity<Object> optionalConflictHandler(
            RuntimeException ex, WebRequest request) {
        String bodyOfResponse = String.format("Could not parse optional value: %s.", ex.getMessage());
        return new ResponseEntity<>(bodyOfResponse, HttpStatus.BAD_REQUEST);
    }
}
