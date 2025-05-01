package com.onrank.server.common.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Map;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(CustomException.class)
    protected ResponseEntity<ErrorResponse> handleCustomException(CustomException ex) {

        CustomErrorInfo customErrorInfo = ex.getCustomErrorInfo();
        ErrorResponse response = ErrorResponse.builder()
                .code(customErrorInfo.name())
                .message(customErrorInfo.getMessage())
                .build();

        return ResponseEntity
                .status(customErrorInfo.getHttpStatus())
                .body(response);
    }

    @ExceptionHandler(Exception.class)
    protected ResponseEntity<Object> handleValidationExceptions(Exception ex) {

        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR) // 500
                .body(Map.of("message", ex.getMessage()));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    protected ResponseEntity<Object> handleValidationExceptions(MethodArgumentNotValidException ex) {

        ErrorResponse response = ErrorResponse.builder()
                .message(ex.getBindingResult().getFieldErrors().get(0).getDefaultMessage())
                .build();

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST) // 400
                .body(response);
    }
}
