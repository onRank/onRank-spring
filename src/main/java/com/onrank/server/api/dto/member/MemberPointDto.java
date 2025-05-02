package com.onrank.server.api.dto.member;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

@Builder
@Schema(description = "member 랭킹 목록용 dto (이름, 점수)")
public record MemberPointDto(

        @Schema(description = "이름", example = "김휘래")
        String studentName,

        @Schema(description = "memberId", example = "1")
        Long memberId,

        @Schema(description = "총점수", example = "100000")
        Long totalPoint
) {
}

