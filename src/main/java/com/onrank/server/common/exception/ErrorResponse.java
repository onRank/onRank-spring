package com.onrank.server.common.exception;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@Schema(description = "Custom 에러 정보 응답 객체")
public class ErrorResponse {

        @Schema(description = "에러 코드", example = "NOT_STUDY_MEMBER")
        private String code;

        @Schema(description = "에러 메시지", example = "해당 스터디에 속한 멤버가 아닙니다.")
        private String message;
}
