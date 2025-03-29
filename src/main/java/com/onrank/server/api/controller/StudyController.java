package com.onrank.server.api.controller;

import com.onrank.server.api.dto.oauth.CustomOAuth2User;
import com.onrank.server.api.dto.student.AddStudyRequest;
import com.onrank.server.api.dto.student.CreateStudyRequestDto;
import com.onrank.server.api.dto.student.CreateStudyResponseDto;
import com.onrank.server.api.dto.study.MainpageStudyResponseDto;
import com.onrank.server.api.service.study.StudyService;
import com.onrank.server.common.util.JWTUtil;
import com.onrank.server.domain.study.Study;
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
    private final JWTUtil JWTUtil;

    @GetMapping
    public ResponseEntity<List<MainpageStudyResponseDto>> getStudiesByUsers(
            @RequestHeader("Authorization") String authHeader) {

        String accessToken = authHeader.substring(7);
        String username = JWTUtil.getUsername(accessToken);

        List<MainpageStudyResponseDto> studies = studyService.getStudiesByUsername(username);

        log.info("studies: {}", studies);

        return ResponseEntity.ok().body(studies);
    }

    @PostMapping("/add")
    public ResponseEntity<Map<String, Object>> createStudy(
            @RequestBody AddStudyRequest addStudyRequest,
            @AuthenticationPrincipal CustomOAuth2User oAuth2User) {

        String username = oAuth2User.getName();
        return ResponseEntity.status(HttpStatus.CREATED).body(studyService.createStudy(addStudyRequest, username));

//        // 사용자 정보를 포함하여 스터디 생성
//        Study study = studyService.createStudy(addStudyRequest, username);
//
//        AddStudyResponse responseDto = new AddStudyResponse(
//                study.getStudyId(),
//                "Study created with id: " + study.getStudyId()
//        );
//
//        return ResponseEntity
//                .status(HttpStatus.CREATED)
//                .body(responseDto);
    }
}