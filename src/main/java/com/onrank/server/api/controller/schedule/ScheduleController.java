package com.onrank.server.api.controller.schedule;

import com.onrank.server.api.dto.common.ContextResponse;
import com.onrank.server.api.dto.common.MemberStudyContext;
import com.onrank.server.api.dto.oauth.CustomOAuth2User;
import com.onrank.server.api.dto.schedule.AddScheduleRequest;
import com.onrank.server.api.dto.schedule.ScheduleResponse;
import com.onrank.server.api.service.member.MemberService;
import com.onrank.server.api.service.schedule.ScheduleService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/studies/{studyId}/schedules")
@RequiredArgsConstructor
public class ScheduleController {

    private final ScheduleService scheduleService;
    private final MemberService memberService;

    /**
     * 스터디 내 모든 일정 조회 (스터디 멤버만 가능)
     */
    @GetMapping
    public ResponseEntity<ContextResponse<List<ScheduleResponse>>> getSchedules(
            @PathVariable Long studyId,
            @AuthenticationPrincipal CustomOAuth2User oAuth2User) {

        // 스터디 멤버만 가능
        if (!memberService.isMemberInStudy(oAuth2User.getName(), studyId)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        MemberStudyContext context = memberService.getContext(oAuth2User.getName(), studyId);
        List<ScheduleResponse> schedules = scheduleService.getScheduleResponsesByStudyId(studyId);

        return ResponseEntity.ok(new ContextResponse<>(context, schedules));
    }

    @PostMapping("/add")
    public ResponseEntity<MemberStudyContext> createSchedule(
            @PathVariable Long studyId,
            @RequestBody AddScheduleRequest request,
            @AuthenticationPrincipal CustomOAuth2User oAuth2User) {


        scheduleService.createSchedule(oAuth2User.getName(), studyId, request);
        MemberStudyContext context = memberService.getContext(oAuth2User.getName(), studyId);

        return ResponseEntity.status(HttpStatus.CREATED).body(context);
    }

    // 스케줄 수정
    @PutMapping("/{scheduleId}")
    public ResponseEntity<MemberStudyContext> updateSchedule(
            @PathVariable Long studyId,
            @PathVariable Long scheduleId,
            @RequestBody AddScheduleRequest request,
            @AuthenticationPrincipal CustomOAuth2User oAuth2User) {

        // CREATOR, HOST 만 가능
        if (!memberService.isMemberCreatorOrHost(oAuth2User.getName(), studyId)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        scheduleService.updateSchedule(scheduleId, request);
        MemberStudyContext context = memberService.getContext(oAuth2User.getName(), studyId);
        return ResponseEntity.ok(context);
    }

    // 스케줄 삭제
    @DeleteMapping("/{scheduleId}")
    public ResponseEntity<MemberStudyContext> deleteSchedule(
            @PathVariable Long studyId,
            @PathVariable Long scheduleId,
            @AuthenticationPrincipal CustomOAuth2User oAuth2User) {

        // CREATOR, HOST 만 가능
        if (!memberService.isMemberCreatorOrHost(oAuth2User.getName(), studyId)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        scheduleService.deleteSchedule(scheduleId, studyId);
        MemberStudyContext context = memberService.getContext(oAuth2User.getName(), studyId);
        return ResponseEntity.ok(context);
    }
}
