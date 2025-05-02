package com.onrank.server.api.dto.assignment;

import com.onrank.server.api.dto.file.FileMetadataDto;
import com.onrank.server.domain.submission.SubmissionStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

import java.time.LocalDateTime;
import java.util.List;

@Builder
@Schema(description = "과제 상세 조회용 과제 상세 응답 DTO")
public record AssignmentDetailResponse(

        @Schema(description = "과제 ID", example = "1")
        Long assignmentId,

        @Schema(description = "과제 제목", example = "퀵정렬 구현")
        String assignmentTitle,

        @Schema(description = "과제 제출 상태", example = "SUBMITTED")
        SubmissionStatus submissionStatus,

        @Schema(description = "과제 마감일", example = "2025-05-01T23:59:00")
        LocalDateTime assignmentDueDate,

        @Schema(description = "과제 배점", example = "100")
        Long assignmentMaxPoint,

        @Schema(description = "과제 내용", example = "퀵정렬을 재귀로 구현하고 분석하세요.")
        String assignmentContent,

        @Schema(description = "과제 파일 목록", implementation = FileMetadataDto.class)
        List<FileMetadataDto> assignmentFiles,

        @Schema(description = "제출 내용 - SUBMITTED 이상일 때만 포함 아니면 빈 문자열 (\"\")", example = "퀵정렬 구현 코드입니다.")
        String submissionContent,                   // SUBMITTED 이상일 때만

        @Schema(description = "제출 파일 목록 - SUBMITTED 이상일 때만 포함, 아니면 빈 배열([])", implementation = FileMetadataDto.class)
        List<FileMetadataDto> submissionFiles,      // SUBMITTED 이상일 때만

        @Schema(description = "제출 점수 - SCORED일 때만 포함, 아니면 null", example = "95")
        Integer submissionScore,                    // SCORED일 때만

        @Schema(description = "제출 코멘트 - SCORED일 때만 포함, 아니면 null", example = "성능 최적화가 인상적입니다.")
        String submissionComment                    // SCORED일 때만
) {
}