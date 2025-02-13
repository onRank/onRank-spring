package com.onrank.server.domain.student;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class StudentDto {

    @NotBlank(message = "이름은 필수 항목입니다.")
    @Size(max = 50, message = "이름은 50자를 초과할 수 없습니다.")
    private String name;

    @NotBlank(message = "부서는 필수 항목입니다.")
    @Size(max = 50, message = "부서는 50자를 초과할 수 없습니다.")
    private String department;

    @NotBlank(message = "전화번호는 필수 항목입니다.")
    @Size(max = 20, message = "전화번호는 20자를 초과할 수 없습니다.")
    private String phoneNumber;

    // DTO에서 엔티티로 변환
    public Student toEntity(String email) {
        return Student.builder()
                .name(name)
                .email(email) // Google OAuth2에서 제공한 email
                .department(department)
                .phoneNumber(phoneNumber)
                .build();
    }
}
