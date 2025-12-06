package io.npee.holidaykeeper.web.advice;

import io.npee.holidaykeeper.web.controller.dto.ErrorResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponse> handleIllegalArgument(IllegalArgumentException ex) {
        ErrorResponse body = ErrorResponse.builder()
                .code("BAD_REQUEST")
                .message(ex.getMessage())
                .build();

        return ResponseEntity
                .badRequest()
                .body(body);
    }
}
