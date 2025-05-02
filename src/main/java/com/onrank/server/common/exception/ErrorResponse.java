package com.onrank.server.common.exception;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

@Builder
@Schema(description = "Custom 에러 정보 응답 객체")
public record ErrorResponse(

        @Schema(description = "에러 코드", example = "NOT_STUDY_MEMBER")
        String code,

        @Schema(description = "에러 메시지", example = "해당 스터디에 속한 멤버가 아닙니다.")
        String message
) {}
