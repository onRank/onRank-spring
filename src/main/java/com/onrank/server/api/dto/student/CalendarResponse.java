package com.onrank.server.api.dto.student;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

@Schema(description = "스터디 단위 캘린더 응답 DTO")
public record CalendarResponse(

        @Schema(description = "스터디 이름", example = "알고리즘 스터디")
        String studyName,

        @Schema(description = "스터디 캘린더 색상 코드", example = "#FF5733")
        String colorCode,

        @Schema(description = "해당 스터디의 일정/과제 항목 리스트")
        List<CalendarDetailResponse> detailList
) {}
