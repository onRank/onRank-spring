package com.onrank.server.api.controller.assignment;

import com.onrank.server.api.dto.assignment.*;
import com.onrank.server.api.dto.common.ContextResponse;
import com.onrank.server.api.dto.file.PresignedUrlResponse;
import com.onrank.server.api.dto.oauth.CustomOAuth2User;
import com.onrank.server.api.dto.submission.UpdateSubmissionRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PathVariable;
import io.swagger.v3.oas.annotations.parameters.RequestBody;

import java.util.List;

@Tag(name = "과제 API", description = "스터디 과제 관련 API")
public interface AssignmentControllerDocs {

    @Operation(summary = "과제 업로드", description = "스터디에 과제를 업로드합니다. HOST 또는 CREATOR만 접근 가능합니다.", security = {@SecurityRequirement(name = "bearer-key")})
    @ApiResponse(responseCode = "201", description = "과제 생성 성공")
    ResponseEntity<ContextResponse<List<PresignedUrlResponse>>> createAssignment(
            @Parameter(description = "스터디 ID", example = "1") @PathVariable Long studyId,
            @RequestBody(description = "과제 생성 요청", required = true,
                    content = @Content(schema = @Schema(implementation = CreateAssignmentRequest.class),
                            examples = @ExampleObject(value = """
                            {
                                "assignmentTitle": "퀵정렬 구현 과제",
                                "assignmentContent": "퀵정렬을 재귀로 구현하고 시간복잡도를 분석하세요.",
                                "assignmentDueDate": "2025-05-01T23:59:00",
                                "assignmentMaxPoint": 100,
                                "fileNames": ["QuickSort.java", "README.md"]
                            }
                            """)))
            CreateAssignmentRequest request,
            @AuthenticationPrincipal CustomOAuth2User oAuth2User
    );

    @Operation(summary = "과제 수정 페이지 조회", description = "기존 과제 정보를 수정 페이지에서 불러옵니다. HOST 또는 CREATOR만 접근할 수 있습니다.", security = {@SecurityRequirement(name = "bearer-key")})
    @ApiResponse(responseCode = "200", description = "과제 수정 정보 조회 성공")
    ResponseEntity<ContextResponse<AssignmentEditResponse>> getAssignmentForEdit(
            @Parameter(description = "스터디 ID", example = "1") @PathVariable Long studyId,
            @Parameter(description = "과제 ID", example = "10") @PathVariable Long assignmentId,
            @AuthenticationPrincipal CustomOAuth2User oAuth2User
    );

    @Operation(summary = "과제 수정", description = "과제 정보를 수정합니다. HOST 또는 CREATOR만 접근할 수 있습니다.", security = {@SecurityRequirement(name = "bearer-key")})
    @ApiResponse(responseCode = "200", description = "과제 수정 성공")
    ResponseEntity<ContextResponse<List<PresignedUrlResponse>>> updateAssignment(
            @Parameter(description = "스터디 ID", example = "1") @PathVariable Long studyId,
            @Parameter(description = "과제 ID", example = "10") @PathVariable Long assignmentId,
            @RequestBody(description = "과제 수정 요청", required = true,
                    content = @Content(schema = @Schema(implementation = UpdateAssignmentRequest.class),
                            examples = @ExampleObject(value = """
                            {
                                "assignmentTitle": "퀵정렬 개선 과제",
                                "assignmentContent": "퀵정렬을 반복문으로도 구현해 보세요.",
                                "assignmentDueDate": "2025-05-07T23:59:00",
                                "assignmentMaxPoint": 120,
                                "remainingFileIds": [101, 102],
                                "newFileNames": ["ImprovedQuickSort.java"]
                            }
                            """)))
            UpdateAssignmentRequest request,
            @AuthenticationPrincipal CustomOAuth2User oAuth2User
    );

    @Operation(summary = "과제 삭제", description = "해당 과제를 삭제합니다. HOST 또는 CREATOR만 접근할 수 있습니다.", security = {@SecurityRequirement(name = "bearer-key")})
    @ApiResponse(responseCode = "200", description = "과제 삭제 성공")
    ResponseEntity<ContextResponse<Void>> deleteAssignment(
            @Parameter(description = "스터디 ID", example = "1") @PathVariable Long studyId,
            @Parameter(description = "과제 ID", example = "10") @PathVariable Long assignmentId,
            @AuthenticationPrincipal CustomOAuth2User oAuth2User
    );

    @Operation(summary = "과제 목록 조회", description = "스터디에 등록된 과제 목록을 조회합니다. 스터디 멤버만 접근할 수 있습니다.", security = {@SecurityRequirement(name = "bearer-key")})
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "과제 목록 조회 성공"),
            @ApiResponse(responseCode = "403", description = "스터디 멤버가 아님", content = @Content(schema = @Schema(hidden = true)))
    })
    ResponseEntity<ContextResponse<List<AssignmentListResponse>>> getAssignments(
            @Parameter(description = "스터디 ID", example = "1") @PathVariable Long studyId,
            @AuthenticationPrincipal CustomOAuth2User oAuth2User
    );

    @Operation(summary = "과제 상세 조회", description = "특정 과제의 상세 정보를 조회합니다. 스터디 멤버만 접근할 수 있습니다.", security = {@SecurityRequirement(name = "bearer-key")})
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "과제 상세 조회 성공"),
            @ApiResponse(responseCode = "403", description = "스터디 멤버가 아님", content = @Content(schema = @Schema(hidden = true)))
    })
    ResponseEntity<ContextResponse<AssignmentDetailResponse>> getAssignmentDetail(
            @Parameter(description = "스터디 ID", example = "1") @PathVariable Long studyId,
            @Parameter(description = "과제 ID", example = "10") @PathVariable Long assignmentId,
            @AuthenticationPrincipal CustomOAuth2User oAuth2User
    );

    @Operation(summary = "과제 제출", description = "과제에 대한 제출물을 업로드합니다. 스터디 멤버만 접근할 수 있습니다.", security = {@SecurityRequirement(name = "bearer-key")})
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "제출물 업로드 성공"),
            @ApiResponse(responseCode = "403", description = "스터디 멤버가 아님", content = @Content(schema = @Schema(hidden = true)))
    })
    ResponseEntity<ContextResponse<List<PresignedUrlResponse>>> submitAssignment(
            @Parameter(description = "스터디 ID", example = "1") @PathVariable Long studyId,
            @Parameter(description = "과제 ID", example = "10") @PathVariable Long assignmentId,
            @RequestBody(description = "제출 요청", required = true,
                    content = @Content(schema = @Schema(implementation = CreateSubmissionRequest.class),
                            examples = @ExampleObject(value = """
                            {
                                "submissionContent": "퀵정렬을 구현한 코드입니다.",
                                "fileNames": ["QuickSort.java", "Result.txt"]
                            }
                            """)))
            CreateSubmissionRequest request,
            @AuthenticationPrincipal CustomOAuth2User oAuth2User
    );

    @Operation(summary = "과제 재제출", description = "제출한 과제를 수정하여 재제출합니다. 스터디 멤버만 접근할 수 있습니다.", security = {@SecurityRequirement(name = "bearer-key")})
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "재제출 성공"),
            @ApiResponse(responseCode = "403", description = "스터디 멤버가 아님", content = @Content(schema = @Schema(hidden = true)))
    })
    ResponseEntity<ContextResponse<List<PresignedUrlResponse>>> resubmitAssignment(
            @Parameter(description = "스터디 ID", example = "1") @PathVariable Long studyId,
            @Parameter(description = "과제 ID", example = "10") @PathVariable Long assignmentId,
            @RequestBody(description = "재제출 요청", required = true,
                    content = @Content(schema = @Schema(implementation = UpdateSubmissionRequest.class),
                            examples = @ExampleObject(value = """
                            {
                                "submissionContent": "코드를 최적화했습니다.",
                                "remainingFileIds": [201, 202],
                                "newFileNames": ["OptimizedQuickSort.java"]
                            }
                            """)))
            UpdateSubmissionRequest request,
            @AuthenticationPrincipal CustomOAuth2User oAuth2User
    ) throws IllegalAccessException;
}
