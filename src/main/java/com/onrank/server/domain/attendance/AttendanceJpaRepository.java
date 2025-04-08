package com.onrank.server.domain.attendance;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface AttendanceJpaRepository extends JpaRepository<Attendance, Long> {

    Optional<Attendance> findFirstByScheduleScheduleId(Long scheduleId);

    List<Attendance> findAllByMemberMemberId(Long memberId);

    List<Attendance> findAllByScheduleScheduleId(Long scheduleId);

    Optional<Attendance> findByAttendanceId(Long attendanceId);
}
