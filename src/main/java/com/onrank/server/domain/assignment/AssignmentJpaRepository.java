package com.onrank.server.domain.assignment;

import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface AssignmentJpaRepository extends JpaRepository<Assignment, Long> {

    Optional<Assignment> findByAssignmentId(Long assignmentId);

    List<Assignment> findByStudyStudyId(Long studyId);

    List<Assignment> findByAssignmentDueDateBetween(LocalDateTime start, LocalDateTime end);
}
