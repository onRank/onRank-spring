package com.onrank.server.domain.study;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface StudyJpaRepository extends JpaRepository<Study, Long> {

    Optional<Study> findByStudyId(Long id);
}
