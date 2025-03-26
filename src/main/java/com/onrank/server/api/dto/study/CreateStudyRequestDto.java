package com.onrank.server.api.dto.study;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Schema(description = "스터디 생성 요청 DTO")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CreateStudyRequestDto {

    @NotBlank
    @Schema(description = "스터디 이름", example = "프로그래밍 스터디")
    private String studyName;

    @NotBlank
    @Schema(description = "스터디 소개 내용", example = "자바의 정석으로 함께 공부하는 스터디")
    private String studyContent;

    @Schema(description = "스터디 이미지 URL", example = "https://example.com/image.png")
    private String studyImageUrl;

    @Schema(description = "스터디 신청 구글폼 URL", example = "https://forms.gle/example")
    private String studyGoogleFormUrl;
}
