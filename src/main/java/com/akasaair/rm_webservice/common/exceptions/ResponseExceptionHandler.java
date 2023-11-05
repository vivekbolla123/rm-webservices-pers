package com.akasaair.rm_webservice.common.exceptions;


import com.akasaair.rm_webservice.common.APIResponse;
import com.akasaair.rm_webservice.common.config.Error;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;

@ControllerAdvice
@Slf4j
public class ResponseExceptionHandler {

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

    @ExceptionHandler(DataMismatchedException.class)
    public ResponseEntity<APIResponse<APIResponse.Error>> handleException(DataMismatchedException e) {
        APIResponse.Error error = APIResponse.Error.builder()
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .message(e.message)
                .build();
        return ResponseEntity.status(error.getStatus()).body(new APIResponse<>(error));
    }
}