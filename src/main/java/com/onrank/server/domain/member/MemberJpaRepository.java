package com.onrank.server.domain.member;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface MemberJpaRepository extends JpaRepository<Member, Long> {

    Optional<Member> findByStudentStudentIdAndStudyStudyId(Long studentId, Long studyId);

    List<Member> findAllByStudy_StudyId(Long studyId);
}
