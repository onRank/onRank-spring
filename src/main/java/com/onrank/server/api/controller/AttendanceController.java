package com.onrank.server.api.controller;

import com.onrank.server.api.dto.attendance.AttendanceMemberResponse;
import com.onrank.server.api.dto.attendance.AttendanceResponse;
import com.onrank.server.api.dto.oauth.CustomOAuth2User;
import com.onrank.server.api.service.attendance.AttendanceService;
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
@RequestMapping("/studies/{studyId}/attendances")
@RequiredArgsConstructor
public class AttendanceController {

    private final MemberService memberService;
    private final AttendanceService attendanceService;

    /**
     * 출석 목록 조회 (스터디 멤버만 가능)
     */
    @GetMapping
    public ResponseEntity<List<AttendanceResponse>> getAttendances(
            @PathVariable Long studyId,
            @AuthenticationPrincipal CustomOAuth2User oAuth2User) {

        // 스터디 멤버만 가능
        if (!memberService.isMemberInStudy(oAuth2User.getName(), studyId)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        return ResponseEntity.ok(attendanceService.getAttendanceResponsesByStudyId(studyId));
    }

    /**
     * 특정 일정의 출석 상세 조회 (HOST 만 가능)
     */
    @GetMapping("/{scheduleId}")
    public ResponseEntity<List<AttendanceMemberResponse>> getAttendance(
            @PathVariable Long studyId,
            @PathVariable Long scheduleId,
            @AuthenticationPrincipal CustomOAuth2User oAuth2User) {

        // HOST 만 가능
        if (!memberService.isMemberHost(oAuth2User.getName(), studyId)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        return ResponseEntity.ok(attendanceService.getAttendanceMembersByScheduleId(scheduleId));
    }

    /**
     * 출석 상태 변경 (HOST 만 가능)
     */
    @PutMapping("/{attendanceId}")
    public ResponseEntity<Void> updateAttendanceStatus(
            @PathVariable Long studyId,
            @PathVariable Long attendanceId,
            @RequestParam String status,
            @AuthenticationPrincipal CustomOAuth2User oAuth2User) {

        // HOST 만 가능
        if (!memberService.isMemberHost(oAuth2User.getName(), studyId)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        attendanceService.updateAttendanceStatus(attendanceId, status);
        return ResponseEntity.ok().build();
    }
}
