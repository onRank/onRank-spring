package com.onrank.server.api.controller;

import com.onrank.server.api.dto.error.ErrorResponse;
import com.onrank.server.api.dto.oauth.CustomOAuth2User;
import com.onrank.server.api.dto.study.CreateStudyRequestDto;
import com.onrank.server.api.dto.study.CreateStudyResponseDto;
import com.onrank.server.api.dto.study.StudyListResponseDto;
import com.onrank.server.api.exception.NotFoundException;
import com.onrank.server.api.service.study.StudyService;
import com.onrank.server.domain.study.Study;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.headers.Header;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("studies")
@RequiredArgsConstructor
public class StudyController {

    private final StudyService studyService;

    @Operation(
            summary = "내가 속한 스터디 목록 조회",
            description = "로그인한 사용자가 속한 모든 스터디를 조회합니다."
    )
    @ApiResponse(
            responseCode = "200",
            description = "스터디 목록 조회 성공",
            content = @Content(schema = @Schema(implementation = StudyListResponseDto.class))
    )
    @GetMapping
    public ResponseEntity<List<StudyListResponseDto>> getStudiesByUsers(
            @AuthenticationPrincipal CustomOAuth2User oAuth2User) {

        List<StudyListResponseDto> studies = studyService.getStudiesByUsername(oAuth2User.getName());

        log.info("studies: {}", studies);

        return ResponseEntity.ok().body(studies);
    }

    @Operation(
            summary = "스터디 생성",
            description = "새로운 스터디를 생성합니다.",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "스터디 생성 요청 데이터",
                    required = true,
                    content = @Content(schema = @Schema(implementation = CreateStudyRequestDto.class))
            )
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "201",
                    description = "스터디 생성 성공",
                    headers = {
                            @Header(
                                    name = "Location",
                                    description = "생성된 스터디의 URI",
                                    schema = @Schema(type = "string", example = "/studies/123"))
                    }
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "요청 데이터 오류",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "서버 내부 오류",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            )
    })
    @PostMapping
    public ResponseEntity<CreateStudyResponseDto> createStudy(
            @Valid @RequestBody CreateStudyRequestDto requestDto,
            @AuthenticationPrincipal CustomOAuth2User oAuth2User) {

        try {
            Study study = studyService.createStudy(requestDto, oAuth2User.getName());

            URI studyURI = URI.create("/studies/" + study.getStudyId());

            return ResponseEntity.created(studyURI).build();

        } catch (IllegalArgumentException e) {
            throw new NotFoundException("스터디 생성 중 오류: " + e.getMessage());
        }
    }
}
