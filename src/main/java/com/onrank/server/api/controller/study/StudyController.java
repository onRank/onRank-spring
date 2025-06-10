package com.onrank.server.api.controller.study;

import com.onrank.server.api.dto.common.ContextResponse;
import com.onrank.server.api.dto.oauth.CustomOAuth2User;
import com.onrank.server.api.dto.study.*;
import com.onrank.server.api.service.study.StudyService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/studies")
@RequiredArgsConstructor
public class StudyController implements StudyControllerDocs {

    private final StudyService studyService;

    @GetMapping
    public ResponseEntity<List<StudyListResponse>> getStudies(
            @AuthenticationPrincipal CustomOAuth2User oAuth2User) {
        return ResponseEntity.ok(studyService.getStudyListResponsesByUsername(oAuth2User.getName()));
    }

    @GetMapping("/{studyId}")
    public ResponseEntity<ContextResponse<StudyPageResponse>> getStudy(
            @PathVariable Long studyId,
            @AuthenticationPrincipal CustomOAuth2User oAuth2User) {
        return ResponseEntity.ok(studyService.getStudyPage(oAuth2User.getName(), studyId));
    }

    @PostMapping("/add")
    public ResponseEntity<AddStudyResponse> createStudy(
            @RequestBody AddStudyRequest addStudyRequest,
            @AuthenticationPrincipal CustomOAuth2User oAuth2User) {
        return ResponseEntity.status(HttpStatus.CREATED).body(studyService.createStudy(addStudyRequest, oAuth2User.getName()));
    }
}