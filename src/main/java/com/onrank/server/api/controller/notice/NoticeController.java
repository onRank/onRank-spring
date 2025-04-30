package com.onrank.server.api.controller.notice;

import com.onrank.server.api.dto.common.ContextResponse;
import com.onrank.server.api.dto.common.MemberStudyContext;
import com.onrank.server.api.dto.file.PresignedUrlResponse;
import com.onrank.server.api.dto.notice.AddNoticeRequest;
import com.onrank.server.api.dto.notice.NoticeListResponse;
import com.onrank.server.api.dto.notice.NoticeDetailResponse;
import com.onrank.server.api.dto.notice.UpdateNoticeRequest;
import com.onrank.server.api.dto.oauth.CustomOAuth2User;
import com.onrank.server.api.service.notice.NoticeService;
import com.onrank.server.api.service.member.MemberService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/studies/{studyId}/notices")
@RequiredArgsConstructor
public class NoticeController implements NoticeControllerDocs {

    private final NoticeService noticeService;
    private final MemberService memberService;

    /**
     * 공지사항 목록 조회 (스터디 멤버만 가능)
     */
    @GetMapping
    public ResponseEntity<ContextResponse<List<NoticeListResponse>>> getNotices(
            @PathVariable Long studyId,
            @AuthenticationPrincipal CustomOAuth2User oAuth2User) {
        return ResponseEntity.ok(noticeService.getNotices(oAuth2User.getName(), studyId));
    }

    /**
     * 특정 공지사항 조회 (스터디 멤버만 가능)
     */
    @GetMapping("/{noticeId}")
    public ResponseEntity<ContextResponse<NoticeDetailResponse>> getNotice(
            @PathVariable Long studyId,
            @PathVariable Long noticeId,
            @AuthenticationPrincipal CustomOAuth2User oAuth2User) {
        return ResponseEntity.ok(noticeService.getNoticeDetail(oAuth2User.getName(), studyId, noticeId));
    }

    /**
     * 공지사항 등록 (CREATOR, HOST 만 가능)
     */
    @PostMapping("/add")
    public ResponseEntity<ContextResponse<List<PresignedUrlResponse>>> createNotice(
            @PathVariable Long studyId,
            @RequestBody AddNoticeRequest request,
            @AuthenticationPrincipal CustomOAuth2User oAuth2User) {
        return ResponseEntity.status(HttpStatus.CREATED).body(noticeService.createNotice(oAuth2User.getName(), studyId, request));
    }

    /**
     * 공지사항 수정 (CREATOR, HOST 만 가능)
     */
    @PutMapping("/{noticeId}")
    public ResponseEntity<ContextResponse<List<PresignedUrlResponse>>> updateNotice(
            @PathVariable Long studyId,
            @PathVariable Long noticeId,
            @RequestBody UpdateNoticeRequest request,
            @AuthenticationPrincipal CustomOAuth2User oAuth2User) {
        return ResponseEntity.ok(noticeService.updateNotice(oAuth2User.getName(), studyId, noticeId, request));
    }

    /**
     * 공지사항 삭제 (CREATOR, HOST 만 가능)
     */
    @DeleteMapping("/{noticeId}")
    public ResponseEntity<MemberStudyContext> deleteNotice(
            @PathVariable Long studyId,
            @PathVariable Long noticeId,
            @AuthenticationPrincipal CustomOAuth2User oAuth2User) {
        return ResponseEntity.ok(noticeService.deleteNotice(oAuth2User.getName(), studyId, noticeId));
    }
}
