package com.onrank.server.api.controller;

import com.onrank.server.api.dto.notice.AddNoticeRequest;
import com.onrank.server.api.dto.notice.NoticeIdResponse;
import com.onrank.server.api.dto.notice.NoticeResponse;
import com.onrank.server.api.dto.oauth.CustomOAuth2User;
import com.onrank.server.api.service.notice.NoticeService;
import com.onrank.server.api.service.study.StudyService;
import com.onrank.server.api.service.member.MemberService;
import com.onrank.server.domain.notice.Notice;
import com.onrank.server.domain.study.Study;
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
public class NoticeController {

    private final NoticeService noticeService;
    private final StudyService studyService;
    private final MemberService memberService;

    /**
     * 스터디 내 모든 공지사항 조회 (스터디 멤버만 가능)
     */
    @GetMapping
    public ResponseEntity<List<NoticeResponse>> getNotices(
            @PathVariable Long studyId,
            @AuthenticationPrincipal CustomOAuth2User oAuth2User) {

        // 스터디 멤버만 가능
        if (!memberService.isMemberInStudy(oAuth2User.getName(), studyId)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        return ResponseEntity.ok(noticeService.getNoticeResponsesByStudyId(studyId));
    }

    /**
     * 특정 공지사항 조회 (스터디 멤버만 가능)
     */
    @GetMapping("/{noticeId}")
    public ResponseEntity<NoticeResponse> getNotice(
            @PathVariable Long studyId,
            @PathVariable Long noticeId,
            @AuthenticationPrincipal CustomOAuth2User oAuth2User) {

        // 스터디 멤버만 가능
        if (!memberService.isMemberInStudy(oAuth2User.getName(), studyId)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        Notice notice = noticeService.findByNoticeId(noticeId)
                .orElseThrow(() -> new IllegalArgumentException("해당 공지사항이 존재하지 않습니다."));

        return ResponseEntity.ok(new NoticeResponse(notice));
    }

    /**
     * 공지사항 등록 (HOST 만 가능)
     */
    @PostMapping("/add")
    public ResponseEntity<NoticeIdResponse> createNotice(
            @PathVariable Long studyId,
            @RequestBody AddNoticeRequest addNoticeRequest,
            @AuthenticationPrincipal CustomOAuth2User oAuth2User) {

        // HOST 만 가능
        if (!memberService.isMemberHost(oAuth2User.getName(), studyId)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        Study study = studyService.findByStudyId(studyId)
                .orElseThrow(() -> new IllegalArgumentException("해당 스터디가 존재하지 않습니다."));

        Notice notice = addNoticeRequest.toEntity(study);
        noticeService.createNotice(notice);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new NoticeIdResponse(notice.getNoticeId()));
    }

    /**
     * 공지사항 수정 (HOST 만 가능)
     */
    @PutMapping("/{noticeId}")
    public ResponseEntity<NoticeIdResponse> updateNotice(
            @PathVariable Long studyId,
            @PathVariable Long noticeId,
            @RequestBody AddNoticeRequest addNoticeRequest,
            @AuthenticationPrincipal CustomOAuth2User oAuth2User) {

        // HOST 만 가능
        if (!memberService.isMemberHost(oAuth2User.getName(), studyId)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        noticeService.updateNotice(noticeId, addNoticeRequest.getNoticeTitle(),
                addNoticeRequest.getNoticeContent(), addNoticeRequest.getNoticeImagePath());

        return ResponseEntity.ok(new NoticeIdResponse(noticeId));
    }

    /**
     * 공지사항 삭제 (HOST 만 가능)
     */
    @DeleteMapping("/{noticeId}")
    public ResponseEntity<NoticeIdResponse> deleteNotice(
            @PathVariable Long studyId,
            @PathVariable Long noticeId,
            @AuthenticationPrincipal CustomOAuth2User oAuth2User) {

        // HOST 만 가능
        if (!memberService.isMemberHost(oAuth2User.getName(), studyId)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        noticeService.deleteNotice(noticeId);

        return ResponseEntity.ok(new NoticeIdResponse(noticeId));
    }
}
