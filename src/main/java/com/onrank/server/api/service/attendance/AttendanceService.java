package com.onrank.server.api.service.attendance;

import com.onrank.server.api.dto.attendance.AttendanceMemberResponse;
import com.onrank.server.api.dto.attendance.AttendanceResponse;
import com.onrank.server.api.service.member.MemberService;
import com.onrank.server.domain.attendance.Attendance;
import com.onrank.server.domain.attendance.AttendanceJpaRepository;
import com.onrank.server.domain.attendance.AttendanceStatus;
import com.onrank.server.domain.member.Member;
import com.onrank.server.domain.member.MemberJpaRepository;
import com.onrank.server.domain.schedule.Schedule;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AttendanceService {

    private final AttendanceJpaRepository attendanceRepository;
    private final MemberJpaRepository memberRepository;
    private final MemberService memberService;

    // 새로운 일정(schedule) 등록 시 자동으로 Attendance 생성
    @Transactional
    public void createAttendancesForSchedule(Schedule schedule) {
        // 해당 스터디의 모든 멤버 조회
        List<Member> members = memberRepository.findByStudyStudyId(schedule.getStudy().getStudyId());

        // 출석 데이터 생성 및 저장
        List<Attendance> attendances = members.stream()
                .map(member -> Attendance.builder()
                        .schedule(schedule)
                        .member(member)
                        .build())
                .collect(Collectors.toList());

        attendanceRepository.saveAll(attendances);
    }

    // 출석 조회를 위한 List<AttendanceResponse> 객체 생성
    public List<AttendanceResponse> getAttendanceResponsesByStudyId(String username, Long studyId) {

        Member member = memberService.findMemberByUsernameAndStudyId(username, studyId)
                .orElseThrow(() -> new IllegalStateException("Member not found"));

        return attendanceRepository.findAllByMemberMemberId(member.getMemberId())
                .stream()
                .map(AttendanceResponse::new)
                .collect(Collectors.toList());
    }

    // 특정 일정(scheduleId)에 속한 모든 출석 정보 List<AttendanceMemberResponse> 조회
    public List<AttendanceMemberResponse> getAttendanceMembersByScheduleId(Long scheduleId) {
        return attendanceRepository.findAllByScheduleScheduleId(scheduleId)
                .stream()
                .map(AttendanceMemberResponse::new)
                .collect(Collectors.toList());
    }

    public String getScheduleTitle(Long scheduleId) {
        return attendanceRepository.findFirstByScheduleScheduleId(scheduleId)
                .map(attendance -> attendance.getSchedule().getScheduleTitle())
                .orElseThrow(() -> new IllegalArgumentException("No attendance found for scheduleId: " + scheduleId));
    }

    public LocalDateTime getScheduleStartingAt(Long scheduleId) {
        return attendanceRepository.findFirstByScheduleScheduleId(scheduleId)
                .map(attendance -> attendance.getSchedule().getScheduleStartingAt())
                .orElseThrow(() -> new IllegalArgumentException("No attendance found for scheduleId: " + scheduleId));
    }

    //출석 상태 변경 (호스트만 가능)
    @Transactional
    public void updateAttendanceStatus(Long attendanceId, String status) {

        // 출석 정보 조회
        Attendance attendance = attendanceRepository.findByAttendanceId(attendanceId)
                .orElseThrow(() -> new IllegalArgumentException("AttendanceId " + attendanceId + " not found"));

        // 유효한 출석 상태 값인지 검증 후 변경
        try {
            AttendanceStatus newStatus = AttendanceStatus.valueOf(status);
            AttendanceStatus oldStatus = attendance.getAttendanceStatus();
            Member member = attendance.getMember();
            member.updateAttendanceCount(oldStatus, newStatus);
            attendance.updateStatus(newStatus);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Attendance status " + status + " is not valid");
        }
    }
}
