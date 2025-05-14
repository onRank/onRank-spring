package com.onrank.server.api.dto.member;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

import java.util.List;

@Schema(description = "(스터디 관리) 멤버 응답 DTO")
@Builder
public record MemberManagementResponse(

        @Schema(description = "스터디 이름", example = "자료구조 스터디")
        String studyName,

        @Schema(description = "스터디 멤버 목록", implementation = MemberListResponse.class)
        List<MemberListResponse> members
) {}
