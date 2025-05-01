package com.onrank.server.api.controller;


import com.onrank.server.common.exception.CustomException;
import com.onrank.server.domain.member.Member;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import static com.onrank.server.common.exception.CustomErrorInfo.NOT_STUDY_MEMBER;

@Slf4j
@RestController
@RequiredArgsConstructor
public class TestController {

    @GetMapping("/test/exception")
    public ResponseEntity<Member> test() {
        throw new CustomException(NOT_STUDY_MEMBER);
    }
}
