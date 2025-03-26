package com.onrank.server.api.dto.study;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Schema(description = "스터디 목록 응답 DTO")
@AllArgsConstructor
@Getter
public class StudyListResponseDto {

    @Schema(description = "스터디 ID", example = "1")
    private Long studyId;

    @Schema(description = "스터디 이름", example = "알고리즘 스터디")
    private String studyName;

    @Schema(description = "스터디 소개", example = "매주 알고리즘 문제 풀이")
    private String studyContent;

    @Schema(description = "스터디 대표 이미지 URL", example = "https://example.com/study-image.jpg")
    private String studyImage;

    @Schema(description = "스터디 지원 구글폼 URL", example = "https://forms.gle/abcdef123")
    private String studyGoogleForm;
}
