package com.onrank.server.api.dto.study;

import com.onrank.server.domain.study.Study;
import com.onrank.server.domain.study.StudyStatus;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "스터디 목록 응답 DTO")
public record MyPageStudyListResponse(
        @Schema(description = "스터디 ID", example = "1")
        Long studyId,

        @Schema(description = "스터디 이름", example = "자료구조 스터디")
        String studyName,

        @Schema(description = "스터디 상태", example = "ACTIVE")
        StudyStatus studyStatus
) {
    public static MyPageStudyListResponse from(Study study) {
        return new MyPageStudyListResponse(
                study.getStudyId(),
                study.getStudyName(),
                study.getStudyStatus()
        );
    }
}
