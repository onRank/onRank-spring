package com.onrank.server.api.controller;

import com.onrank.server.api.dto.notice.NoticeResponse;
import com.onrank.server.api.dto.oauth.CustomOAuth2User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/studies/{studyId}/attendances")
@RequiredArgsConstructor
public class AttendanceController {

//    private final

    /**
     * 스터디 내의 개인 출석 조회 (스터디 참여자)
     */
//    @GetMapping
//    public ResponseEntity<List<NoticeResponse>> getAttendances(
//            @PathVariable Long studyId,
//            @AuthenticationPrincipal CustomOAuth2User oAuth2User) {
//
//        // 스터디 멤버만 가능
//        if (!memberService.isMemberInStudy(oAuth2User.getName(), studyId)) {
//            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
//        }
//
//        return ResponseEntity.ok(noticeService.getNoticeResponsesByStudyId(studyId));
//    }
}
