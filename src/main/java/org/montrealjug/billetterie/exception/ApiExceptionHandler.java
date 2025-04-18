package org.montrealjug.billetterie.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.time.LocalDateTime;

@ControllerAdvice
public class ApiExceptionHandler {
    @ExceptionHandler(value={EntityNotFoundException.class})
    public ResponseEntity<ApiException> handleEntityNotFoundException(EntityNotFoundException exception) {
        ApiException apiException = new ApiException(exception.getMessage(), HttpStatus.NOT_FOUND, LocalDateTime.now());

        return new ResponseEntity<>(apiException, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(value={RequestException.class})
    public ResponseEntity<ApiException> handleRequestException(RequestException exception) {
        ApiException apiException = new ApiException(exception.getMessage(), exception.getHttpStatus(), LocalDateTime.now());

        return new ResponseEntity<>(apiException, exception.getHttpStatus());
    }
}
