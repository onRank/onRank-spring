package com.onrank.server.api.dto.assignment;

import com.onrank.server.api.dto.file.FileMetadataDto;
import com.onrank.server.domain.submission.SubmissionStatus;
import lombok.Builder;

import java.time.LocalDateTime;
import java.util.List;

@Builder
public record AssignmentDetailResponse(
        Long assignmentId,
        String assignmentTitle,
        String assignmentContent,
        LocalDateTime assignmentDueDate,
        Long assignmentMaxPoint,
        SubmissionStatus submissionStatus,
        List<FileMetadataDto> assignmentFiles,

        String submissionContent,                   // SUBMITTED 이상일 때만
        List<FileMetadataDto> submissionFiles,      // SUBMITTED 이상일 때만
        Integer submissionScore,                    // SCORED일 때만
        String submissionComment                    // SCORED일 때만
) {
}