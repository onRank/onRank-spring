package com.onrank.server.domain.assignment;

import com.onrank.server.domain.study.Study;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AssignmentJpaRepository extends JpaRepository<Assignment, Long> {

    List<Assignment> findByStudyStudyId(Long studyId);
}
