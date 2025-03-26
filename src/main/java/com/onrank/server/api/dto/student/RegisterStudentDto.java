package com.onrank.server.api.dto.student;

import com.onrank.server.domain.student.Role;
import com.onrank.server.domain.student.Student;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Set;

@Schema(description = "회원 등록 요청 DTO")
@Getter
@NoArgsConstructor
public class RegisterStudentDto {

    @NotBlank(message = "이름은 필수입니다.")
    @Schema(description = "학생 이름", example = "홍길동")
    private String studentName;


    @NotBlank(message = "전화번호는 필수입니다.")
    @Schema(description = "전화번호", example = "010-1234-5678")
    private String studentPhoneNumber;

    @Schema(description = "학교명", example = "서울대학교")
    private String studentSchool;

    @Schema(description = "학과명", example = "컴퓨터공학과")
    private String studentDepartment;

    public Student toEntity(String username, String studentEmail, Set<Role> roles) {
        return Student.builder()
                .studentName(studentName)
                .studentPhoneNumber(studentPhoneNumber)
                .studentSchool(studentSchool)
                .studentDepartment(studentDepartment)
                .username(username)
                .studentEmail(studentEmail)
                .roles(roles)
                .build();
    }
}
