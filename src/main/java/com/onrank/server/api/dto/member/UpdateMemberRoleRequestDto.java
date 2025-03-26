package com.onrank.server.api.dto.member;

import com.onrank.server.domain.member.MemberRole;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;

@Schema(description = "스터디 멤버 역할 변경 요청 DTO")
@Getter
public class UpdateMemberRoleRequestDto {

    @Schema(description = "변경할 역할", example = "HOST")
    @NotNull(message = "멤버 역할은 필수입니다.")
    private MemberRole memberRole;
}
