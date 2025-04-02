package com.onrank.server.api.controller.study;

import com.onrank.server.api.dto.oauth.CustomOAuth2User;
import com.onrank.server.api.dto.study.AddStudyRequest;
import com.onrank.server.api.dto.study.AddStudyResponse;
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
@RequestMapping("/studies")
@RequiredArgsConstructor
public class StudyController {

    private final StudyService studyService;

    @GetMapping
    public ResponseEntity<List<StudyListResponse>> getStudies(
            @AuthenticationPrincipal CustomOAuth2User oAuth2User) {

        return ResponseEntity.ok(studyService.getStudyListResponsesByUsername(oAuth2User.getName()));
    }

    @PostMapping("/add")
    public ResponseEntity<AddStudyResponse> createStudy(
            @RequestBody AddStudyRequest addStudyRequest,
            @AuthenticationPrincipal CustomOAuth2User oAuth2User) {

        return ResponseEntity.status(HttpStatus.CREATED).body(studyService.createStudy(addStudyRequest, oAuth2User.getName()));
    }
}