package com.onrank.server.domain.submission;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SubmissionJpaRepository extends JpaRepository<Submission, Long> {

    List<Submission> findAllByMemberMemberId(Long memberId);
}
