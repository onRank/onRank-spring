package com.onrank.server.api.controller.submission;

import com.onrank.server.api.dto.common.ContextResponse;
import com.onrank.server.api.dto.oauth.CustomOAuth2User;
import com.onrank.server.api.dto.submission.ScoreSubmissionRequest;
import com.onrank.server.api.dto.submission.SubmissionDetailResponse;
import com.onrank.server.api.dto.submission.SubmissionListResponse;
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
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

@Tag(name = "제출물 API", description = "스터디 과제 제출물 관련 관리자 기능 API")
public interface SubmissionControllerDocs {

    @Operation(
            summary = "제출물 목록 조회",
            description = "특정 과제의 모든 제출물을 조회합니다. CREATOR 또는 HOST만 접근할 수 있습니다.",
            security = {@SecurityRequirement(name = "bearer-key")}
    )
    @ApiResponse(responseCode = "200", description = "제출물 목록 조회 성공")
    ResponseEntity<ContextResponse<List<SubmissionListResponse>>> getSubmissions(
            @Parameter(description = "스터디 ID", example = "1") @PathVariable Long studyId,
            @Parameter(description = "과제 ID", example = "5") @PathVariable Long assignmentId,
            @AuthenticationPrincipal CustomOAuth2User oAuth2User
    );

    @Operation(
            summary = "제출물 상세 조회",
            description = "특정 제출물의 상세 정보를 조회합니다. CREATOR 또는 HOST만 접근할 수 있습니다.",
            security = {@SecurityRequirement(name = "bearer-key")}
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "제출물 상세 조회 성공"),
            @ApiResponse(responseCode = "403", description = "권한 없음", content = @Content(schema = @Schema(hidden = true)))
    })
    ResponseEntity<ContextResponse<SubmissionDetailResponse>> getSubmissionDetail(
            @Parameter(description = "스터디 ID", example = "1") @PathVariable Long studyId,
            @Parameter(description = "과제 ID", example = "5") @PathVariable Long assignmentId,
            @Parameter(description = "제출물 ID", example = "27") @PathVariable Long submissionId,
            @AuthenticationPrincipal CustomOAuth2User oAuth2User
    );

    @Operation(
            summary = "제출물 채점",
            description = "제출물의 점수 및 코멘트를 기록합니다. CREATOR 또는 HOST만 접근할 수 있습니다.",
            security = {@SecurityRequirement(name = "bearer-key")}
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "채점 성공"),
            @ApiResponse(responseCode = "403", description = "권한 없음", content = @Content(schema = @Schema(hidden = true)))
    })
    ResponseEntity<ContextResponse<Void>> scoreSubmission(
            @Parameter(description = "스터디 ID", example = "1") @PathVariable Long studyId,
            @Parameter(description = "과제 ID", example = "5") @PathVariable Long assignmentId,
            @Parameter(description = "제출물 ID", example = "27") @PathVariable Long submissionId,
            @RequestBody @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "채점 요청",
                    required = true,
                    content = @Content(
                            schema = @Schema(implementation = ScoreSubmissionRequest.class),
                            examples = @ExampleObject(value = """
                                {
                                    "submissionScore": 95,
                                    "submissionComment": "테스트 케이스 모두 통과했습니다. 매우 우수한 구현입니다."
                                }
                                """)
                    )
            ) ScoreSubmissionRequest request,
            @AuthenticationPrincipal CustomOAuth2User oAuth2User
    );
}
