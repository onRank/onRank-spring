package com.onrank.server.domain.schedule;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ScheduleJpaRepository extends JpaRepository<Schedule, Long> {

    List<Schedule> findAllByStudyStudyId(Long studyId);

    Optional<Schedule> findByScheduleId(Long scheduleId);
}
