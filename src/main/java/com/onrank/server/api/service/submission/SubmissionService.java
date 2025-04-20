package com.onrank.server.api.service.submission;

import com.onrank.server.domain.assignment.Assignment;
import com.onrank.server.domain.member.Member;
import com.onrank.server.domain.submission.Submission;
import com.onrank.server.domain.submission.SubmissionJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
public class SubmissionService {

    private final SubmissionJpaRepository submissionRepository;

    public Submission findByAssignmentAndMember(Assignment assignment, Member member) {
        return submissionRepository.findByAssignmentAndMember(assignment, member)
                .orElseThrow(() -> new NoSuchElementException("Assignment not found"));
    }
}