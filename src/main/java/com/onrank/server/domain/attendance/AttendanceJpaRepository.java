package com.onrank.server.domain.attendance;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface AttendanceJpaRepository extends JpaRepository<Attendance, Long> {

    // 스터디의 모든 출석 리스트 조회
    @Query("SELECT a FROM Attendance a WHERE a.schedule.study.studyId = :studyId")
    List<Attendance> findAllByStudyId(@Param("studyId") Long studyId);

    // 특정 일정(scheduleId)에 속한 모든 출석 정보 조회
    @Query("SELECT a FROM Attendance a WHERE a.schedule.scheduleId = :scheduleId")
    List<Attendance> findAllByScheduleId(@Param("scheduleId") Long scheduleId);

    Optional<Attendance> findByAttendanceId(Long scheduleId);
}