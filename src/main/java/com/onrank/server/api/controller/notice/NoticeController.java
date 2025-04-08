package com.onrank.server.api.controller.notice;

import com.onrank.server.api.dto.file.FileMetadataDto;
import com.onrank.server.api.dto.member.MemberRoleResponse;
import com.onrank.server.api.dto.notice.AddNoticeRequest;
import com.onrank.server.api.dto.notice.NoticeContext;
import com.onrank.server.api.dto.notice.NoticeResponse;
import com.onrank.server.api.dto.oauth.CustomOAuth2User;
import com.onrank.server.api.service.notice.NoticeService;
import com.onrank.server.api.service.study.StudyService;
import com.onrank.server.api.service.member.MemberService;
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
    public ResponseEntity<NoticeContext<List<NoticeResponse>>> getNotices(
            @PathVariable Long studyId,
            @AuthenticationPrincipal CustomOAuth2User oAuth2User) {

        // 스터디 멤버만 가능
        if (!memberService.isMemberInStudy(oAuth2User.getName(), studyId)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        MemberRoleResponse context = memberService.getMyRoleInStudy(oAuth2User.getName(), studyId);
        List<NoticeResponse> notices = noticeService.getNoticeResponsesByStudyId(studyId);

        return ResponseEntity.ok(new NoticeContext<>(context, notices));
    }

    /**
     * 특정 공지사항 조회 (스터디 멤버만 가능)
     */
    @GetMapping("/{noticeId}")
    public ResponseEntity<NoticeContext<NoticeResponse>> getNotice(
            @PathVariable Long studyId,
            @PathVariable Long noticeId,
            @AuthenticationPrincipal CustomOAuth2User oAuth2User) {

        // 스터디 멤버만 가능
        if (!memberService.isMemberInStudy(oAuth2User.getName(), studyId)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        MemberRoleResponse context = memberService.getMyRoleInStudy(oAuth2User.getName(), studyId);
        NoticeResponse notice = noticeService.getNoticeResponse(noticeId);

        return ResponseEntity.ok(new NoticeContext<>(context, notice));
    }

    /**
     * 공지사항 등록 (HOST 만 가능)
     */
    @PostMapping("/add")
    public ResponseEntity<NoticeContext<List<FileMetadataDto>>> createNotice(
            @PathVariable Long studyId,
            @RequestBody AddNoticeRequest addNoticeRequest,
            @AuthenticationPrincipal CustomOAuth2User oAuth2User) {

        // HOST 만 가능
        if (!memberService.isMemberCreatorOrHost(oAuth2User.getName(), studyId)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        Study study = studyService.findByStudyId(studyId)
                .orElseThrow(() -> new IllegalArgumentException("Study not found"));

        // Pre-signed URL 생성 및 파일 메타데이터 저장
        List<FileMetadataDto> presignedDtos = noticeService.createNotice(addNoticeRequest, study);
        MemberRoleResponse context = memberService.getMyRoleInStudy(oAuth2User.getName(), studyId);

        return ResponseEntity.status(HttpStatus.CREATED).body(new NoticeContext<>(context, presignedDtos));
    }

    /**
     * 공지사항 수정 (HOST 만 가능)
     */
    @PutMapping("/{noticeId}")
    public ResponseEntity<NoticeContext<List<FileMetadataDto>>> updateNotice(
            @PathVariable Long studyId,
            @PathVariable Long noticeId,
            @RequestBody AddNoticeRequest addNoticeRequest,
            @AuthenticationPrincipal CustomOAuth2User oAuth2User) {

        // HOST 만 가능
        if (!memberService.isMemberCreatorOrHost(oAuth2User.getName(), studyId)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        List<FileMetadataDto> fileDtos = noticeService.updateNotice(noticeId, addNoticeRequest);
        MemberRoleResponse context = memberService.getMyRoleInStudy(oAuth2User.getName(), studyId);

        return ResponseEntity.ok(new NoticeContext<>(context, fileDtos));
    }

    /**
     * 공지사항 삭제 (HOST 만 가능)
     */
    @DeleteMapping("/{noticeId}")
    public ResponseEntity<MemberRoleResponse> deleteNotice(
            @PathVariable Long studyId,
            @PathVariable Long noticeId,
            @AuthenticationPrincipal CustomOAuth2User oAuth2User) {

        // HOST 만 가능
        if (!memberService.isMemberCreatorOrHost(oAuth2User.getName(), studyId)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        noticeService.deleteNotice(noticeId);
        MemberRoleResponse context = memberService.getMyRoleInStudy(oAuth2User.getName(), studyId);
        return ResponseEntity.ok(context);

    }
}
