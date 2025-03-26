package com.onrank.server.api.dto.member;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

@Schema(description = "스터디 멤버 추가 요청 DTO")
@Getter
public class AddMemberRequestDto {

    @Schema(description = "추가할 학생 이메일", example = "student@example.com")
    @NotBlank(message = "학생 이메일은 필수입니다.")
    @Email(message = "유효한 이메일 형식이어야 합니다.")
    private String studentEmail;
}
