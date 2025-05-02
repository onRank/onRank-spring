package com.onrank.server.api.controller.study;

import com.onrank.server.api.dto.common.ContextResponse;
import com.onrank.server.api.dto.oauth.CustomOAuth2User;
import com.onrank.server.api.dto.study.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "스터디 조회/생성 API", description = "스터디 목록 조회, 단일 스터디 조회, 스터디 생성 기능 제공")
public interface StudyControllerDocs {

    @Operation(summary = "내가 가입한 스터디 목록 조회", description = "로그인한 사용자가 가입한 모든 스터디 목록을 조회합니다.")
    @ApiResponse(responseCode = "200", description = "조회 성공")
    @GetMapping
    ResponseEntity<List<StudyListResponse>> getStudies(
            @Parameter(hidden = true)
            @AuthenticationPrincipal CustomOAuth2User oAuth2User
    );

    @Operation(summary = "스터디 상세 정보 조회", description = "스터디 페이지(랭킹 포함)를 조회합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "조회 성공",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = StudyPageResponse.class)
                    )
            ),
            @ApiResponse(responseCode = "403", description = "스터디에 가입되지 않은 사용자"),
            @ApiResponse(responseCode = "404", description = "스터디를 찾을 수 없음")
    })
    @GetMapping("/{studyId}")
    ResponseEntity<ContextResponse<StudyPageResponse>> getStudy(
            @Parameter(description = "조회할 스터디 ID", required = true)
            @PathVariable Long studyId,

            @Parameter(hidden = true)
            @AuthenticationPrincipal CustomOAuth2User oAuth2User
    );


    @Operation(summary = "스터디 생성", description = "새로운 스터디를 생성합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "생성 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청 데이터")
    })
    @PostMapping("/add")
    ResponseEntity<AddStudyResponse> createStudy(
            @Parameter(description = "생성할 스터디 정보", required = true)
            @RequestBody AddStudyRequest addStudyRequest,

            @Parameter(hidden = true)
            @AuthenticationPrincipal CustomOAuth2User oAuth2User
    );
}
