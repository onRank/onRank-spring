package com.onrank.server.api.controller.student;

import com.onrank.server.api.dto.oauth.CustomOAuth2User;
import com.onrank.server.api.dto.student.AddStudentRequest;
import com.onrank.server.api.dto.student.StudentResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

public interface StudentControllerDocs {

    @Operation(
            summary = "마이페이지 조회",
            description = "OAuth2 인증된 사용자의 마이페이지 정보를 조회합니다.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "조회 성공"),
                    @ApiResponse(responseCode = "401", description = "인증 실패"),
                    @ApiResponse(responseCode = "404", description = "학생 정보 없음")
            }
    )
    @GetMapping
    ResponseEntity<StudentResponse> getStudent(
            @Parameter(hidden = true)
            @AuthenticationPrincipal CustomOAuth2User oAuth2User
    );

    @Operation(
            summary = "학생 정보 수정",
            description = "OAuth2 인증된 사용자의 정보를 수정합니다.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "수정 성공"),
                    @ApiResponse(responseCode = "400", description = "요청 형식 오류"),
                    @ApiResponse(responseCode = "401", description = "인증 실패"),
                    @ApiResponse(responseCode = "403", description = "권한 없음")
            }
    )
    @PutMapping
    ResponseEntity<Void> updateStudent(
            @Parameter(description = "학생 ID", example = "1")
            @PathVariable Long studentId,
            @Valid @RequestBody AddStudentRequest addStudentRequest,
            @Parameter(hidden = true)
            @AuthenticationPrincipal CustomOAuth2User oAuth2User
    );
}
