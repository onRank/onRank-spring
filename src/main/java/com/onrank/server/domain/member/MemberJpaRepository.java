package com.onrank.server.domain.member;

import com.onrank.server.domain.study.Study;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface MemberJpaRepository extends JpaRepository<Member, Long> {

    Optional<Member> findByStudentStudentIdAndStudyStudyId(Long studentId, Long studyId);

    List<Member> findByStudy(Study study);

    List<Member> findByStudyStudyId(Long studyId);

    Optional<Member> findByMemberIdAndStudyStudyId(Long memberId, Long studyId);
}
