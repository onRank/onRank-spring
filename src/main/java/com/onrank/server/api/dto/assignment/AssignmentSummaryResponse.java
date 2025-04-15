package com.onrank.server.api.dto.assignment;

import com.onrank.server.domain.assignment.Assignment;
import com.onrank.server.domain.submission.Submission;
import com.onrank.server.domain.submission.SubmissionStatus;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;

@Schema(description = "과제 목록 조회용 과제 요약 응답 DTO")
public record AssignmentSummaryResponse(

        @Schema(description = "과제 ID", example = "1")
        Long assignmentId,

        @Schema(description = "과제 제목", example = "퀵정렬 구현")
        String assignmentTitle,

        @Schema(description = "과제 마감 기한", example = "2025-05-01T23:59:00")
        LocalDateTime assignmentDueDate,

        @Schema(description = "제출 상태 (NOTSUBMITTED, SUBMITTED, SCORED)", example = "SUBMITTED")
        SubmissionStatus submissionStatus,

        @Schema(description = "채점 점수 (미채점 시 null)", example = "95")
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