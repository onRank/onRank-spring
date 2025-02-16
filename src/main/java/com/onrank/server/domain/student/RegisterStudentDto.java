package com.onrank.server.domain.student;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class RegisterStudentDto {

    @NotBlank(message = "이름은 필수 항목입니다.")
    @Size(max = 50, message = "이름은 50자를 초과할 수 없습니다.")
    private String name;

    @Size(max = 50, message = "학교명은 50자를 초과할 수 없습니다.")
    private String school; // 선택 사항

    @Size(max = 50, message = "학과명은 50자를 초과할 수 없습니다.")
    private String department; // 선택 사항

    @NotBlank(message = "전화번호는 필수 항목입니다.")
    @Size(max = 20, message = "전화번호는 20자를 초과할 수 없습니다.")
    private String phoneNumber;

    // ✅ DTO에서 엔티티로 변환 (email을 매개변수로 받음)
    public Student toEntity(String username, String email) {
        return Student.builder()
                .name(name)
                .username(username) // ✅ Google OAuth2의 sub 값 (PK 역할)
                .email(email) // ✅ OAuth2에서 받은 email 추가
                .school(school) // 선택 값
                .department(department) // 선택 값
                .phoneNumber(phoneNumber)
                .build();
    }
}
