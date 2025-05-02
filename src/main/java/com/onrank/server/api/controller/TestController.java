package com.onrank.server.api.controller;


import com.onrank.server.common.exception.CustomException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import static com.onrank.server.common.exception.CustomErrorInfo.*;

@RestController
@RequiredArgsConstructor
public class TestController {

    @GetMapping("/test/exception")
    public void exception1() {  // @ExceptionHandler({Exception.class})
        throw new IllegalStateException("State Exception...");
    }

    @GetMapping("/test/exception/custom1")
    public void custom_exception1(){  // @ExceptionHandler(CustomException.class)
        throw new CustomException(ACCESS_DENIED);
    }

    @GetMapping("/test/exception/custom2")
    public ResponseEntity<Object> custom_exception2(){  // @ExceptionHandler(CustomException.class)
        throw new CustomException(STUDENT_NOT_FOUND);
    }
}
