package com.onrank.server.api.dto.attendance;

import com.onrank.server.domain.attendance.Attendance;
import com.onrank.server.domain.attendance.AttendanceStatus;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;

@Getter
@RequiredArgsConstructor
public class AttendanceResponse {

    private Long attendanceId;
    private AttendanceStatus attendanceStatus;
    private String scheduleTitle;
    private LocalDateTime scheduleStartingAt;

    public AttendanceResponse(Attendance attendance) {
        this.attendanceId = attendance.getAttendanceId();
        this.attendanceStatus = attendance.getAttendanceStatus();

        this.scheduleTitle = attendance.getSchedule().getScheduleTitle();
        this.scheduleStartingAt = attendance.getSchedule().getScheduleStartingAt();
    }
}