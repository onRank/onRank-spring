package com.onrank.server.domain.attendance;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface AttendanceJpaRepository extends JpaRepository<Attendance, Long> {

    @Query("SELECT a FROM Attendance a WHERE a.schedule.study.studyId = :studyId")
    List<Attendance> findAllByStudyId(@Param("studyId") Long studyId);
}
