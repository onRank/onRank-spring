package com.onrank.server.domain.submission;

import com.onrank.server.domain.assignment.Assignment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface SubmissionJpaRepository extends JpaRepository<Submission, Long> {

    List<Submission> findAllByMemberMemberId(Long memberId);

    Optional<Submission> findByAssignmentAssignmentIdAndMemberMemberId(Long assignmentId, Long memberId);

    List<Submission> findAllByAssignment(Assignment assignment);
}
