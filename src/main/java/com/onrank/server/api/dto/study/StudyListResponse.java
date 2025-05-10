package com.onrank.server.api.dto.study;

import com.onrank.server.api.dto.file.FileMetadataDto;
import com.onrank.server.domain.study.Study;
import com.onrank.server.domain.study.StudyStatus;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "스터디 목록 응답 DTO")
public record StudyListResponse(
        @Schema(description = "스터디 ID", example = "1")
        Long studyId,

        @Schema(description = "스터디 이름", example = "자료구조 스터디")
        String studyName,

        @Schema(description = "스터디 설명", example = "자료구조 개념 정리 및 문제 풀이 스터디입니다.")
        String studyContent,

        @Schema(description = "스터디 상태", example = "ACTIVE")
        StudyStatus studyStatus,

        @Schema(description = "스터디 대표 이미지 정보")
        FileMetadataDto file
) {
    public static StudyListResponse from(Study study, FileMetadataDto fileDto) {
        return new StudyListResponse(
                study.getStudyId(),
                study.getStudyName(),
                study.getStudyContent(),
                study.getStudyStatus(),
                fileDto
        );
    }
}
