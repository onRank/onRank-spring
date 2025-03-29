package com.onrank.server.api.dto.student;

import com.onrank.server.domain.student.Student;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class RegisterStudentRequest {

    @NotBlank
    private String studentName;

    private String studentSchool;

    private String studentDepartment;

    @NotBlank
    private String studentPhoneNumber;

    public Student toEntity(String username, String studentEmail/*, Set<Role> roles*/) {
        return Student.builder()
                .studentName(studentName)
                .studentSchool(studentSchool)
                .studentDepartment(studentDepartment)
                .studentPhoneNumber(studentPhoneNumber)
                .username(username)
                .studentEmail(studentEmail)
//                .roles(roles)
                .build();
    }
}