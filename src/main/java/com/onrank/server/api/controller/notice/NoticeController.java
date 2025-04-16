package com.onrank.server.api.controller.notice;

import com.onrank.server.api.dto.common.ContextResponse;
import com.onrank.server.api.dto.file.FileMetadataDto;
import com.onrank.server.api.dto.common.MemberStudyContext;
import com.onrank.server.api.dto.notice.AddNoticeRequest;
import com.onrank.server.api.dto.notice.NoticeListResponse;
import com.onrank.server.api.dto.notice.NoticeDetailResponse;
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
public class NoticeController implements NoticeControllerDocs {

    private final NoticeService noticeService;
    private final StudyService studyService;
    private final MemberService memberService;

    /**
     * 공지사항 목록 조회 (스터디 멤버만 가능)
     */
    @GetMapping
    public ResponseEntity<ContextResponse<List<NoticeListResponse>>> getNotices(
            @PathVariable Long studyId,
            @AuthenticationPrincipal CustomOAuth2User oAuth2User) {

        // 스터디 멤버만 가능
        if (!memberService.isMemberInStudy(oAuth2User.getName(), studyId)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
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

        // 스터디 멤버만 가능
        if (!memberService.isMemberInStudy(oAuth2User.getName(), studyId)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        return ResponseEntity.ok(noticeService.getNoticeDetail(oAuth2User.getName(), studyId, noticeId));
    }

    /**
     * 공지사항 등록 (CREATOR, HOST 만 가능)
     */
    @PostMapping("/add")
    public ResponseEntity<ContextResponse<List<FileMetadataDto>>> createNotice(
            @PathVariable Long studyId,
            @RequestBody AddNoticeRequest addNoticeRequest,
            @AuthenticationPrincipal CustomOAuth2User oAuth2User) {

        // CREATOR, HOST 만 가능
        if (!memberService.isMemberCreatorOrHost(oAuth2User.getName(), studyId)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        Study study = studyService.findByStudyId(studyId)
                .orElseThrow(() -> new IllegalArgumentException("Study not found"));

        // Pre-signed URL 생성 및 파일 메타데이터 저장
        List<FileMetadataDto> fileDtos = noticeService.createNotice(addNoticeRequest, study);
        MemberStudyContext context = memberService.getContext(oAuth2User.getName(), studyId);

        return ResponseEntity.status(HttpStatus.CREATED).body(new ContextResponse<>(context, fileDtos));
    }

    /**
     * 공지사항 수정 (CREATOR, HOST 만 가능)
     */
    @PutMapping("/{noticeId}")
    public ResponseEntity<ContextResponse<List<FileMetadataDto>>> updateNotice(
            @PathVariable Long studyId,
            @PathVariable Long noticeId,
            @RequestBody AddNoticeRequest addNoticeRequest,
            @AuthenticationPrincipal CustomOAuth2User oAuth2User) {

        // CREATOR, HOST 만 가능
        if (!memberService.isMemberCreatorOrHost(oAuth2User.getName(), studyId)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        List<FileMetadataDto> fileDtos = noticeService.updateNotice(noticeId, addNoticeRequest);
        MemberStudyContext context = memberService.getContext(oAuth2User.getName(), studyId);

        return ResponseEntity.ok(new ContextResponse<>(context, fileDtos));
    }

    /**
     * 공지사항 삭제 (CREATOR, HOST 만 가능)
     */
    @DeleteMapping("/{noticeId}")
    public ResponseEntity<MemberStudyContext> deleteNotice(
            @PathVariable Long studyId,
            @PathVariable Long noticeId,
            @AuthenticationPrincipal CustomOAuth2User oAuth2User) {

        // CREATOR, HOST 만 가능
        if (!memberService.isMemberCreatorOrHost(oAuth2User.getName(), studyId)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        noticeService.deleteNotice(noticeId);
        MemberStudyContext context = memberService.getContext(oAuth2User.getName(), studyId);
        return ResponseEntity.ok(context);

    }
}
