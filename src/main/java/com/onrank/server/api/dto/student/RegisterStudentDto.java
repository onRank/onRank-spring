package com.onrank.server.api.dto.student;

import com.onrank.server.domain.student.Role;
import com.onrank.server.domain.student.Student;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Set;

@Getter
@NoArgsConstructor
public class RegisterStudentDto {

    @NotBlank
    private String studentName;

    private String studentSchool;

    private String studentDepartment;

    @NotBlank
    private String studentPhoneNumber;

    public Student toEntity(String username, String studentEmail, Set<Role> roles) {
        return Student.builder()
                .studentName(studentName)
                .studentSchool(studentSchool)
                .studentDepartment(studentDepartment)
                .studentPhoneNumber(studentPhoneNumber)
                .username(username)
                .studentEmail(studentEmail)
                .roles(roles)
                .build();
    }
}