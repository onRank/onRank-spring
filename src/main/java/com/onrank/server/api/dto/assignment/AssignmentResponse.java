package com.onrank.server.api.dto.assignment;

import com.onrank.server.domain.submission.Submission;
import com.onrank.server.domain.submission.SubmissionStatus;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class AssignmentResponse {

    private Long assignmentId;
    private String assignmentTitle;
    private String assignmentContent;
    private LocalDateTime assignmentDueDate;

    private SubmissionStatus submissionStatus;
    private Integer submissionScore;

    public AssignmentResponse(Submission submission) {
        this.assignmentId = submission.getAssignment().getAssignmentId();
        this.assignmentTitle = submission.getAssignment().getAssignmentTitle();
        this.assignmentContent = submission.getAssignment().getAssignmentContent();
        this.assignmentDueDate = submission.getAssignment().getAssignmentDueDate();

        this.submissionStatus = submission.getSubmissionStatus();
        this.submissionScore = submission.getSubmissionScore(); // null 가능
    }
}
