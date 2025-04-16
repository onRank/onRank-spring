package com.onrank.server.api.controller.notice;

import com.onrank.server.api.dto.common.ContextResponse;
import com.onrank.server.api.dto.notice.NoticeDetailResponse;
import com.onrank.server.api.dto.notice.NoticeListResponse;
import com.onrank.server.api.dto.oauth.CustomOAuth2User;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@Tag(name = "공지사항 API", description = "공지사항 관련 API")
public interface NoticeControllerDocs {

    @Operation(summary = "공지사항 목록 조회", description = "스터디 멤버만 공지사항 목록을 조회할 수 있습니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "공지사항 목록 조회 성공"),
            @ApiResponse(responseCode = "403", description = "스터디 멤버가 아닌 경우 접근 불가")
    })
    @GetMapping("/studies/{studyId}/notices")
    ResponseEntity<ContextResponse<List<NoticeListResponse>>> getNotices(
            @Parameter(description = "스터디 ID", example = "1") @PathVariable Long studyId,
            @Parameter(hidden = true) CustomOAuth2User oAuth2User
    );

    @Operation(summary = "공지사항 상세 조회", description = "스터디 멤버만 특정 공지사항의 상세 정보를 조회할 수 있습니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "공지사항 상세 조회 성공"),
            @ApiResponse(responseCode = "403", description = "스터디 멤버가 아닌 경우 접근 불가"),
            @ApiResponse(responseCode = "404", description = "해당 ID의 공지사항이 존재하지 않음")
    })
    @GetMapping("/studies/{studyId}/notices/{noticeId}")
    ResponseEntity<ContextResponse<NoticeDetailResponse>> getNotice(
            @Parameter(description = "스터디 ID", example = "1") @PathVariable Long studyId,
            @Parameter(description = "공지사항 ID", example = "5") @PathVariable Long noticeId,
            @Parameter(hidden = true) CustomOAuth2User oAuth2User
    );
}
