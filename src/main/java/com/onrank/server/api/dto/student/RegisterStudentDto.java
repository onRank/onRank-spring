package com.onrank.server.api.dto.student;

import com.onrank.server.domain.student.Student;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class RegisterStudentDto {

    private String name;
    private String school;
    private String department;
    private String phoneNumber;

    public Student toEntity(String username, String email) {
        return Student.builder()
                .name(name)
                .username(username)
                .email(email)
                .school(school)
                .department(department)
                .phoneNumber(phoneNumber)
                .build();
    }
}
