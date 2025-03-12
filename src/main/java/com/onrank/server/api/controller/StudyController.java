package com.onrank.server.api.controller;

import com.onrank.server.api.dto.student.CreateStudyRequestDto;
import com.onrank.server.api.dto.student.CreateStudyResponseDto;
import com.onrank.server.api.dto.study.MainpageStudyResponseDto;
import com.onrank.server.api.service.study.StudyService;
import com.onrank.server.api.service.token.TokenService;
import com.onrank.server.domain.study.Study;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("studies")
@RequiredArgsConstructor
public class StudyController {

    private final StudyService studyService;
    private final TokenService tokenService;

    @GetMapping
    public ResponseEntity<List<MainpageStudyResponseDto>> getStudiesByUsers(
            @RequestHeader("Authorization") String authHeader) {

        String accessToken = authHeader.substring(7);
        String username = tokenService.getUsername(accessToken);

        List<MainpageStudyResponseDto> studies = studyService.getStudiesByUsername(username);

        return ResponseEntity.ok().body(studies);
    }

    @PostMapping("/add")
    public ResponseEntity<CreateStudyResponseDto> createStudy(
            @RequestBody CreateStudyRequestDto requestDto) {

        Study study = studyService.createStudy(requestDto);

        CreateStudyResponseDto responseDto = new CreateStudyResponseDto(
                study.getStudyId(),
                "Study created with id: " + study.getStudyId()
        );

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(responseDto);
    }
}