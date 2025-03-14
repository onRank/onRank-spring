package com.onrank.server.api.dto.attendance;

import com.onrank.server.domain.attendance.Attendance;
import com.onrank.server.domain.attendance.AttendanceStatus;
import com.onrank.server.domain.member.MemberRole;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;

@Getter
@RequiredArgsConstructor
public class AttendanceResponse {

    private Long attendanceId;
    private LocalDateTime scheduleStartingAt;
    private AttendanceStatus attendanceStatus;
    private MemberRole memberRole;

    public AttendanceResponse(Attendance attendance) {
        this.attendanceId = attendance.getAttendanceId();
        this.attendanceStatus = attendance.getAttendanceStatus();
        this.scheduleStartingAt = attendance.getSchedule().getScheduleStartingAt();
        this.memberRole = attendance.getMember().getMemberRole();
    }
}