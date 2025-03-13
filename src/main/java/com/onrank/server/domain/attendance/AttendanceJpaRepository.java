package com.onrank.server.domain.attendance;

import org.springframework.data.jpa.repository.JpaRepository;

public interface AttendanceJpaRepository extends JpaRepository<Attendance, Long> {
}
