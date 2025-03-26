package com.onrank.server.domain.member;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface MemberJpaRepository extends JpaRepository<Member, Long> {

    Optional<Member> findByStudentStudentIdAndStudyStudyId(Long studentId, Long studyId);

    List<Member> findByStudyStudyId(Long studyId);

    @Modifying
    @Query("update Member m set m.memberRole = :newMemberRole where m.memberId = :memberId")
    void updateMemberRole(@Param("memberId") Long memberId, @Param("newMemberRole") MemberRole newMemberRole);
}
