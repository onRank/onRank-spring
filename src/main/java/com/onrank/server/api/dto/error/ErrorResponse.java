package com.onrank.server.api.dto.error;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Schema(description = "에러 응답 형식")
public class ErrorResponse {

    @Schema(description = "HTTP 상태 코드", example = "404")
    private final int status;

    @Schema(description = "에러 메시지", example = "해당 자원을 찾을 수 없습니다.")
    private final String message;

    @Schema(description = "에러 코드", example = "NOT_FOUND")
    private final String errorCode;

    @Schema(description = "에러 발생 시각", example = "2025-03-27T12:34:56")
    private final String timestamp;

    @Schema(description = "요청 경로", example = "/studies/1")
    private final String path;

    @Schema(description = "요청 추적 ID", example = "550e8400-e29b-41d4-a716-446655440000")
    private final String traceId;

    public ErrorResponse(int status, String message, String errorCode, String path) {
        this.status = status;
        this.message = message;
        this.errorCode = errorCode;
        this.path = path;
        this.timestamp = LocalDateTime.now().toString();
        this.traceId = UUID.randomUUID().toString();
    }
}
