package com.onrank.server.common.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.Map;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler({CustomException.class})
    public ResponseEntity<ErrorResponse> handleCustomException(CustomException ex) {

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

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(
            MethodArgumentNotValidException ex,
            HttpHeaders headers,
            HttpStatusCode status,
            WebRequest request) {

        ErrorResponse response = ErrorResponse.builder()
                .message(ex.getBindingResult().getFieldErrors().get(0).getDefaultMessage())
                .build();

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST) // 400
                .body(response);
    }
}
