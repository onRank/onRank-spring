package com.onrank.server.api.controller;

import com.onrank.server.api.dto.notice.AddNoticeRequest;
import com.onrank.server.api.dto.notice.NoticeIdResponse;
import com.onrank.server.api.dto.notice.NoticeResponse;
import com.onrank.server.api.service.notice.NoticeService;
import com.onrank.server.api.service.study.StudyService;
import com.onrank.server.domain.notice.Notice;
import com.onrank.server.domain.study.Study;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequestMapping("/studies/{studyId}/notices")
@RequiredArgsConstructor
public class NoticeController {

    private final NoticeService noticeService;
    private final StudyService studyService;

    /**
     * 스터디 내 모든 공지사항 조회
     */
    @GetMapping
    public ResponseEntity<List<NoticeResponse>> getNotices(@PathVariable Long studyId) {
        List<NoticeResponse> notices = noticeService.findByStudyId(studyId)
                .stream()
                .map(NoticeResponse::new)
                .collect(Collectors.toList());

        return ResponseEntity.ok(notices);
    }

    /**
     * 공지사항 등록
     */
    @PostMapping("/add")
    public ResponseEntity<NoticeIdResponse> createNotice(
            @PathVariable Long studyId,
            @RequestBody AddNoticeRequest request) {

        Study study = studyService.findById(studyId)
                .orElseThrow(() -> new IllegalArgumentException("해당 스터디가 존재하지 않습니다."));

        Notice notice = request.toEntity(study);
        noticeService.createNotice(notice);

        return ResponseEntity.status(HttpStatus.CREATED).body(new NoticeIdResponse(notice.getId()));
    }

    /**
     * 특정 공지사항 조회
     */
    @GetMapping("/{noticeId}")
    public ResponseEntity<NoticeResponse> getNotice(
            @PathVariable Long noticeId) {

        Notice notice = noticeService.findById(noticeId)
                .orElseThrow(() -> new IllegalArgumentException("해당 공지사항이 존재하지 않습니다."));

        return ResponseEntity.ok(new NoticeResponse(notice));
    }

    /**
     * 공지사항 수정
     */
    @PutMapping("/{noticeId}")
    public ResponseEntity<NoticeIdResponse> updateNotice(
            @PathVariable Long noticeId,
            @RequestBody AddNoticeRequest request) {

        noticeService.updateNotice(noticeId, request.getTitle(), request.getContent(), request.getImagePath());

        Notice updatedNotice = noticeService.findById(noticeId)
                .orElseThrow(() -> new IllegalArgumentException("해당 공지사항이 존재하지 않습니다."));

        return ResponseEntity.ok(new NoticeIdResponse(noticeId));
    }

    /**
     * 공지사항 삭제
     */
    @DeleteMapping("/{noticeId}")
    public ResponseEntity<NoticeIdResponse> deleteNotice(@PathVariable Long noticeId) {

        noticeService.deleteNotice(noticeId);

        return ResponseEntity.ok(new NoticeIdResponse(noticeId));
    }
}
