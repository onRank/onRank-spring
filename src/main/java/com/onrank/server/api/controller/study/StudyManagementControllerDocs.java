package com.onrank.server.api.controller.study;

import com.onrank.server.api.dto.common.ContextResponse;
import com.onrank.server.api.dto.file.PresignedUrlResponse;
import com.onrank.server.api.dto.auth.CustomOAuth2User;
import com.onrank.server.api.dto.study.StudyUpdateRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@Tag(name = "스터디 관리 API", description = "스터디 정보 수정 및 삭제 기능 제공 (CREATOR, HOST만 접근 가능)")
public interface StudyManagementControllerDocs {

    @Operation(
            summary = "스터디 정보 수정",
            description = "스터디 이름, 설명, 포인트 정책, 상태 등을 수정합니다. 이미지 파일명(newFileName)이 포함되면 기존 이미지도 교체됩니다."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "스터디 정보 수정 성공"),
            @ApiResponse(responseCode = "403", description = "접근 권한 없음"),
            @ApiResponse(responseCode = "404", description = "스터디를 찾을 수 없음")
    })
    @PutMapping
    ResponseEntity<ContextResponse<PresignedUrlResponse>> updateStudy(
            @Parameter(description = "수정할 스터디 ID", required = true)
            @PathVariable Long studyId,

            @Parameter(description = "수정할 스터디 정보", required = true)
            @RequestBody StudyUpdateRequest studyUpdateRequest,

            @Parameter(hidden = true)
            @AuthenticationPrincipal CustomOAuth2User oAuth2User
    );

    @Operation(
            summary = "스터디 삭제",
            description = "스터디 및 관련 데이터(공지, 과제, 게시글, 파일 등)를 삭제합니다. CREATOR 또는 HOST만 수행 가능합니다."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "스터디 삭제 성공"),
            @ApiResponse(responseCode = "403", description = "접근 권한 없음"),
            @ApiResponse(responseCode = "404", description = "스터디를 찾을 수 없음")
    })
    @DeleteMapping
    ResponseEntity<Void> deleteStudy(
            @Parameter(description = "삭제할 스터디 ID", required = true)
            @PathVariable Long studyId,

            @Parameter(hidden = true)
            @AuthenticationPrincipal CustomOAuth2User oAuth2User
    );
}
