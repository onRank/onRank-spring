package com.onrank.server.api.controller;

import com.onrank.server.api.service.study.StudyService;
import com.onrank.server.api.service.token.TokenService;
import com.onrank.server.domain.study.Study;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("studies")
@RequiredArgsConstructor
public class StudyController {

    private final StudyService studyService;
    private final TokenService tokenService;

    @GetMapping("/")
    public ResponseEntity<List<Study>> getStudiesByUsers(@RequestHeader("Authorization") String authHeader) {

        String accessToken = authHeader.substring(7);
        String username = tokenService.getUsername(accessToken);

        List<Study> studies = studyService.getStudiesByUsername(username);

        return ResponseEntity.ok().body(studies);
    }

}