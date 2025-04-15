package com.onrank.server.api.controller.assignment;

import com.onrank.server.api.dto.assignment.AddAssignmentRequest;
import com.onrank.server.api.dto.common.ContextResponse;
import com.onrank.server.api.dto.file.PresignedUrlResponse;
import com.onrank.server.api.dto.oauth.CustomOAuth2User;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

@Tag(name = "과제 API", description = "스터디 과제 관련 API")
public interface AssignmentControllerDocs {

    @Operation(
            summary = "과제 업로드",
            description = "스터디에 과제를 업로드합니다. HOST 또는 CREATOR만 접근 가능합니다.",
            security = {@SecurityRequirement(name = "bearer-key")}
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "과제 생성 성공"),
            @ApiResponse(responseCode = "403", description = "권한 없음"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청"),
            @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    ResponseEntity<ContextResponse<List<PresignedUrlResponse>>> createAssignment(
            @Parameter(description = "스터디 ID", example = "1") @PathVariable Long studyId,
            @RequestBody AddAssignmentRequest request,
            @AuthenticationPrincipal CustomOAuth2User oAuth2User
    );
}
