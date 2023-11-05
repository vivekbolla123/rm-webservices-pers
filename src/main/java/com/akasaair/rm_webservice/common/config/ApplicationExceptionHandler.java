package com.akasaair.rm_webservice.common.config;


import com.akasaair.rm_webservice.common.exceptions.ResourceNotFoundException;
import com.akasaair.rm_webservice.common.exceptions.UnauthorizedException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;

@ControllerAdvice
public class ApplicationExceptionHandler {

    @ResponseStatus(value = HttpStatus.UNAUTHORIZED,
            reason = "clientId clientPassword mismatch")
    @ExceptionHandler(UnauthorizedException.class)
    public ResponseEntity handleException(UnauthorizedException e) {
        Error error = new Error(HttpStatus.UNAUTHORIZED, e.getMessage());
        return new ResponseEntity<>(error, error.getHttpStatus());
    }

    @ResponseStatus(value = HttpStatus.NOT_FOUND, reason = "no resource found")
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity handleException(ResourceNotFoundException e) {
        Error error = new Error(HttpStatus.NOT_FOUND, e.getMessage());
        return new ResponseEntity<>(error, error.getHttpStatus());
    }
}