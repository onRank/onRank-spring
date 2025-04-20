package com.onrank.server.api.controller.assignment;

import com.onrank.server.api.dto.assignment.AssignmentDetailResponse;
import com.onrank.server.api.dto.assignment.AssignmentListResponse;
import com.onrank.server.api.dto.assignment.CreateAssignmentRequest;
import com.onrank.server.api.dto.assignment.CreateSubmissionRequest;
import com.onrank.server.api.dto.common.ContextResponse;
import com.onrank.server.api.dto.file.PresignedUrlResponse;
import com.onrank.server.api.dto.oauth.CustomOAuth2User;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
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
            @ApiResponse(responseCode = "201", description = "과제 생성 성공")
    })
    ResponseEntity<ContextResponse<List<PresignedUrlResponse>>> createAssignment(
            @Parameter(description = "스터디 ID", example = "1") @PathVariable Long studyId,
            @RequestBody CreateAssignmentRequest request,
            @AuthenticationPrincipal CustomOAuth2User oAuth2User
    );

    @Operation(
            summary = "과제 목록 조회",
            description = "스터디에 등록된 과제 목록을 조회합니다. 스터디에 참여한 멤버만 접근할 수 있습니다.",
            security = {@SecurityRequirement(name = "bearer-key")}
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "과제 목록 조회 성공"),
            @ApiResponse(responseCode = "403", description = "스터디 멤버가 아님", content = @Content(schema = @Schema(hidden = true)))
    })
    ResponseEntity<ContextResponse<List<AssignmentListResponse>>> getAssignments(
            @Parameter(description = "스터디 ID", example = "1") @PathVariable Long studyId,
            @AuthenticationPrincipal CustomOAuth2User oAuth2User
    );

    @Operation(
            summary = "과제 상세 조회",
            description = "특정 과제의 상세 정보를 조회합니다. 스터디에 참여한 멤버만 접근할 수 있습니다.",
            security = {@SecurityRequirement(name = "bearer-key")}
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "과제 상세 조회 성공"),
            @ApiResponse(responseCode = "403", description = "스터디 멤버가 아님", content = @Content(schema = @Schema(hidden = true)))
    })
    ResponseEntity<ContextResponse<AssignmentDetailResponse>> getAssignmentDetail(
            @Parameter(description = "스터디 ID", example = "1") @PathVariable Long studyId,
            @Parameter(description = "과제 ID", example = "10") @PathVariable Long assignmentId,
            @AuthenticationPrincipal CustomOAuth2User oAuth2User
    );

    @Operation(
            summary = "제출물 업로드",
            description = "과제에 대한 제출물을 업로드합니다. 스터디 멤버만 접근할 수 있습니다.",
            security = {@SecurityRequirement(name = "bearer-key")}
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "제출물 업로드 성공"),
            @ApiResponse(responseCode = "403", description = "스터디 멤버가 아님", content = @Content(schema = @Schema(hidden = true)))
    })
    ResponseEntity<ContextResponse<List<PresignedUrlResponse>>> createSubmission(
            @Parameter(description = "스터디 ID", example = "1") @PathVariable Long studyId,
            @Parameter(description = "과제 ID", example = "10") @PathVariable Long assignmentId,
            @RequestBody CreateSubmissionRequest request,
            @AuthenticationPrincipal CustomOAuth2User oAuth2User
    );
}