package com.onrank.server.api.dto.attendance;

import com.onrank.server.domain.attendance.Attendance;
import com.onrank.server.domain.attendance.AttendanceStatus;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class AttendanceMemberResponse {

    private Long attendanceId;
    private AttendanceStatus attendanceStatus;
    private Long memberId;
    private String studentName;

    public AttendanceMemberResponse(Attendance attendance) {
        this.attendanceId = attendance.getAttendanceId();
        this.attendanceStatus = attendance.getAttendanceStatus();
        this.memberId = attendance.getMember().getMemberId();
        this.studentName = attendance.getMember().getStudent().getStudentName();
    }
}
