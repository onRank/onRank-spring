package com.onrank.server.api.controller;

import com.onrank.server.api.dto.oauth.CustomOAuth2User;
import com.onrank.server.api.dto.study.AddStudyRequest;
import com.onrank.server.api.dto.study.StudyListResponse;
import com.onrank.server.api.service.study.StudyService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("studies")
@RequiredArgsConstructor
public class StudyController {

    private final StudyService studyService;

    @GetMapping
    public ResponseEntity<List<StudyListResponse>> getStudies(
            @AuthenticationPrincipal CustomOAuth2User oAuth2User) {

        String username = oAuth2User.getName();

        return ResponseEntity.ok(studyService.getStudyListResponsesByUsername(username));
    }

    @PostMapping("/add")
    public ResponseEntity<Map<String, Object>> createStudy(
            @RequestBody AddStudyRequest addStudyRequest,
            @AuthenticationPrincipal CustomOAuth2User oAuth2User) {

        String username = oAuth2User.getName();
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(studyService.createStudy(addStudyRequest, username));
    }
}