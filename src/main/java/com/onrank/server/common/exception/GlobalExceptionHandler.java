package com.onrank.server.common.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.security.access.AccessDeniedException;

import java.util.Map;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(CustomException.class)
    public ResponseEntity<Object> handleCustomException(CustomException ex) {

        CustomErrorInfo customErrorInfo = ex.getCustomErrorInfo();
        ErrorResponse response = ErrorResponse.builder()
                .code(customErrorInfo.name())
                .message(customErrorInfo.getMessage())
                .build();

        return ResponseEntity
                .status(customErrorInfo.getHttpStatus())
                .body(response);
    }

    @ExceptionHandler({Exception.class})
    public ResponseEntity<Object> handleALlExceptions(Exception ex) {

        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR) // 500
                .body(Map.of("message", ex.getMessage()));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Object> handleValidationException(MethodArgumentNotValidException ex) {
        CustomErrorInfo errorInfo = CustomErrorInfo.INVALID_REQUEST;
        ErrorResponse response = ErrorResponse.builder()
            .code(errorInfo.name())
            .message("입력값이 올바르지 않습니다.")
            .build();
        return ResponseEntity.status(errorInfo.getHttpStatus()).body(response);
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<Object> handleAccessDeniedException(AccessDeniedException ex) {
        CustomErrorInfo errorInfo = CustomErrorInfo.ACCESS_DENIED;
        ErrorResponse response = ErrorResponse.builder()
            .code(errorInfo.name())
            .message(errorInfo.getMessage())
            .build();
        return ResponseEntity.status(errorInfo.getHttpStatus()).body(response);
    }
}
