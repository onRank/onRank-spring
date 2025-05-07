package com.onrank.server.domain.assignment;

import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface AssignmentJpaRepository extends JpaRepository<Assignment, Long> {

    List<Assignment> findByStudyStudyId(Long studyId);

    List<Assignment> findByAssignmentDueDateBetween(LocalDateTime start, LocalDateTime end);
}
