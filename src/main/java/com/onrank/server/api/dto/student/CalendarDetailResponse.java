package com.onrank.server.api.dto.student;

import com.onrank.server.domain.notification.NotificationCategory;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

import java.time.LocalDateTime;

@Schema(description = "캘린더 세부 항목 DTO")
@Builder
public record CalendarDetailResponse(

        @Schema(description = "항목 제목", example = "5주차 알고리즘 과제")
        String title,

        @Schema(description = "관련 URL", example = "/studies/assignments/13")
        String relatedUrl,

        @Schema(description = "항목 카테고리(SCHEDULE, ASSIGNMENT)", example = "ASSIGNMENT")
        NotificationCategory category,

        @Schema(description = "일정 시간", example = "2025-06-01T23:59:00")
        LocalDateTime time
) {}
