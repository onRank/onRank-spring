package com.onrank.server.api.dto.student;

import com.onrank.server.domain.student.Student;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class RegisterStudentDto {

    @NotBlank
    private String studentName;

    private String studentSchool;

    private String studentDepartment;

    @NotBlank
    private String studentPhoneNumber;

    public Student toEntity(String username, String studentEmail) {
        return Student.builder()
                .studentName(studentName)
                .studentEmail(studentEmail)
                .studentSchool(studentSchool)
                .studentDepartment(studentDepartment)
                .studentPhoneNumber(studentPhoneNumber)
                .username(username)
                .build();
    }
}