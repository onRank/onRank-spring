package com.onrank.server.api.dto.study;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Schema(description = "스터디 생성 응답 DTO")
@Getter
@AllArgsConstructor
public class CreateStudyResponseDto {

    @Schema(description = "생성된 스터디 ID", example = "1")
    private Long studyId;

    @Schema(description = "성공 메시지", example = "스터디가 성공적으로 생성되었습니다.")
    private String message;
}
