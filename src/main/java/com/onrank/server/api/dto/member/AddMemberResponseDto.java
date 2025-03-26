package com.onrank.server.api.dto.member;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Schema(description = "스터디 멤버 응답 DTO")
@Getter
@AllArgsConstructor
public class AddMemberResponseDto {

    @Schema(description = "멤버 ID", example = "10")
    private Long memberId;

    @Schema(description = "학생 이름", example = "홍길동")
    private String studentName;

    @Schema(description = "학생 이메일", example = "hong@example.com")
    private String studentEmail;
}
