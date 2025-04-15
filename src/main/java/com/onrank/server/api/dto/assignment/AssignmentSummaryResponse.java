package com.onrank.server.api.dto.assignment;

import com.onrank.server.domain.assignment.Assignment;
import com.onrank.server.domain.submission.Submission;
import com.onrank.server.domain.submission.SubmissionStatus;

import java.time.LocalDateTime;


public record AssignmentSummaryResponse(
        Long assignmentId,
        String assignmentTitle,
        LocalDateTime assignmentDueDate,
        SubmissionStatus submissionStatus,
        Integer submissionScore
) {
    public static AssignmentSummaryResponse from(Assignment assignment, Submission submission) {
        return new AssignmentSummaryResponse(
                assignment.getAssignmentId(),
                assignment.getAssignmentTitle(),
                assignment.getAssignmentDueDate(),
                submission.getSubmissionStatus(),
                submission.getSubmissionScore() // null 가능
        );
    }
}