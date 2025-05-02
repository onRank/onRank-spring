package com.onrank.server.api.dto.submission;

import com.onrank.server.api.dto.file.FileMetadataDto;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

import java.time.LocalDateTime;
import java.util.List;

@Builder
@Schema(description = "제출물 상세 조회 응답 DTO")
public record SubmissionDetailResponse(

        @Schema(description = "과제 제목", example = "퀵정렬 구현")
        String assignmentTitle,

        @Schema(description = "과제 마감 기한", example = "2025-05-01T23:59:00")
        LocalDateTime assignmentDueDate,

        @Schema(description = "제출자 ID", example = "101")
        Long memberId,

        @Schema(description = "제출자 이름", example = "이주영")
        String memberName,

        @Schema(description = "제출자 이메일", example = "lee@example.com")
        String memberEmail,

        @Schema(description = "제출 일시", example = "2025-04-20T10:25:30")
        LocalDateTime submissionCreatedAt,

        @Schema(description = "제출 내용", example = "퀵정렬 구현 코드입니다.")
        String submissionContent,

        @Schema(description = "제출 파일 목록", implementation = FileMetadataDto.class)
        List<FileMetadataDto> submissionFiles,

        @Schema(description = "제출 점수 (채점 완료 시)", example = "92")
        Integer submissionScore,

        @Schema(description = "채점 코멘트 (채점 완료 시)", example = "작성 내용이 충실함")
        String submissionComment
) {
}