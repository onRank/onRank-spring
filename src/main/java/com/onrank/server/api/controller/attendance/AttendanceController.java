package com.onrank.server.api.controller.attendance;

import com.onrank.server.api.dto.attendance.*;
import com.onrank.server.api.dto.member.MemberRoleResponse;
import com.onrank.server.api.dto.oauth.CustomOAuth2User;
import com.onrank.server.api.service.attendance.AttendanceService;
import com.onrank.server.api.service.member.MemberService;
import com.onrank.server.api.service.study.StudyService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/studies/{studyId}/attendances")
@RequiredArgsConstructor
public class AttendanceController {

    private final MemberService memberService;
    private final AttendanceService attendanceService;
    private final StudyService studyService;

    /**
     * 출석 목록 조회 (스터디 멤버만 가능)
     */
    @GetMapping
    public ResponseEntity<AttendanceContext<List<AttendanceResponse>>> getAttendances(
            @PathVariable Long studyId,
            @AuthenticationPrincipal CustomOAuth2User oAuth2User) {

        // 스터디 멤버만 가능
        if (!memberService.isMemberInStudy(oAuth2User.getName(), studyId)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        MemberRoleResponse context = memberService.getMyRoleInStudy(oAuth2User.getName(), studyId);
        List<AttendanceResponse> attendances = attendanceService.getAttendanceResponsesByStudyId(oAuth2User.getName(), studyId);

        return ResponseEntity.ok(new AttendanceContext<>(context, attendances));
    }

    /**
     * 특정 일정의 출석 상세 조회 (CREATOR, HOST 만 가능)
     */
    @GetMapping("/{scheduleId}")
    public ResponseEntity<AttendanceDetailContext<List<AttendanceMemberResponse>>> getAttendanceMemberList(
            @PathVariable Long studyId,
            @PathVariable Long scheduleId,
            @AuthenticationPrincipal CustomOAuth2User oAuth2User) {

        // CREATOR, HOST 만 가능
        if (!memberService.isMemberCreatorOrHost(oAuth2User.getName(), studyId)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        List<AttendanceMemberResponse> responses = attendanceService.getAttendanceMembersByScheduleId(scheduleId);
        String scheduleTitle = attendanceService.getScheduleTitle(scheduleId);
        LocalDateTime scheduleStartingAt = attendanceService.getScheduleStartingAt(scheduleId);
        MemberRoleResponse context = memberService.getMyRoleInStudy(oAuth2User.getName(), studyId);

        return ResponseEntity.ok(new AttendanceDetailContext<>(context, scheduleTitle, scheduleStartingAt, responses));
    }

    /**
     * 출석 상태 변경 (CREATOR, HOST 만 가능)
     */
    @PutMapping("/{attendanceId}")
    public ResponseEntity<MemberRoleResponse> updateAttendanceStatus(
            @PathVariable Long studyId,
            @PathVariable Long attendanceId,
            @RequestParam String status,
            @AuthenticationPrincipal CustomOAuth2User oAuth2User) {

        // CREATOR, HOST 만 가능
        if (!memberService.isMemberCreatorOrHost(oAuth2User.getName(), studyId)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        attendanceService.updateAttendanceStatus(attendanceId, status);
        MemberRoleResponse context = memberService.getMyRoleInStudy(oAuth2User.getName(), studyId);

        return ResponseEntity.ok(context);
    }

    /**
     * 출석 POINT 조회
     */
    @GetMapping("/point")
    public ResponseEntity<AttendanceContext<AttendancePointResponse>> getAttendancePoint(
            @PathVariable Long studyId,
            @AuthenticationPrincipal CustomOAuth2User oAuth2User) {
        // 스터디 멤버만 가능
        if (!memberService.isMemberInStudy(oAuth2User.getName(), studyId)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        AttendanceContext<AttendancePointResponse> response = studyService.getAttendancePoint(studyId, oAuth2User.getName());
        return ResponseEntity.ok(response);
    }

    /**
     * 출석 POINT 수정
     */
    @PutMapping("/point")
    public ResponseEntity<MemberRoleResponse> updateAttendancePoint(
            @PathVariable Long studyId,
            @RequestBody @Valid AttendancePointRequest request,
            @AuthenticationPrincipal CustomOAuth2User oAuth2User) {

        // CREATOR, HOST 만 가능
        if (!memberService.isMemberCreatorOrHost(oAuth2User.getName(), studyId)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        studyService.updateAttendancePoint(studyId, request);
        MemberRoleResponse response = memberService.getMyRoleInStudy(oAuth2User.getName(), studyId);
        return ResponseEntity.ok(response);
    }
}
