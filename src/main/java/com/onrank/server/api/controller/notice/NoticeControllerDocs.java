package com.onrank.server.api.controller.notice;

import com.onrank.server.api.dto.common.ContextResponse;
import com.onrank.server.api.dto.file.PresignedUrlResponse;
import com.onrank.server.api.dto.notice.NoticeDetailResponse;
import com.onrank.server.api.dto.notice.NoticeListResponse;
import com.onrank.server.api.dto.notice.UpdateNoticeRequest;
import com.onrank.server.api.dto.oauth.CustomOAuth2User;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

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

    @Operation(summary = "공지사항 수정", description = "공지사항 작성자(CREATOR, HOST)만 공지사항을 수정할 수 있습니다.\n\n" +
            "- 기존 파일 중 유지할 파일 ID 목록과 새로 업로드할 파일 이름 목록을 함께 전달합니다.\n" +
            "- 서버는 유지할 파일을 제외한 나머지 파일을 삭제하고, 새 파일에 대한 Pre-signed URL을 반환합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "공지사항 수정 성공"),
            @ApiResponse(responseCode = "403", description = "공지사항 수정 권한 없음"),
            @ApiResponse(responseCode = "404", description = "해당 ID의 공지사항이 존재하지 않음")
    })
    @PutMapping("/studies/{studyId}/notices/{noticeId}")
    ResponseEntity<ContextResponse<List<PresignedUrlResponse>>> updateNotice(
            @Parameter(description = "스터디 ID", example = "1") @PathVariable Long studyId,
            @Parameter(description = "공지사항 ID", example = "5") @PathVariable Long noticeId,
            @Parameter(description = "공지사항 수정 요청 DTO") @RequestBody UpdateNoticeRequest request,
            @Parameter(hidden = true) CustomOAuth2User oAuth2User
    );

}
