package com.onrank.server.api.dto.student;

import com.onrank.server.api.dto.study.MyPageStudyListResponse;
import com.onrank.server.domain.student.Student;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

@Schema(description = "학생 정보 응답 DTO")
public record StudentResponse(
        @Schema(description = "학생 ID", example = "1")
        Long studentId,

        @Schema(description = "학생 이름", example = "김휘래")
        String studentName,

        @Schema(description = "학교명", example = "아주대학교")
        String studentSchool,

        @Schema(description = "학과", example = "소프트웨어공학과")
        String studentDepartment,

        @Schema(description = "전화번호", example = "010-1234-5678")
        String studentPhoneNumber,

        @Schema(description = "이메일", example = "hwirae@example.com")
        String studentEmail,

        @Schema(description = "가입한 스터디 목록")
        List<MyPageStudyListResponse> studyList
) {
    public static StudentResponse from(Student student, List<MyPageStudyListResponse> studyList) {
        return new StudentResponse(
                student.getStudentId(),
                student.getStudentName(),
                student.getStudentSchool(),
                student.getStudentDepartment(),
                student.getStudentPhoneNumber(),
                student.getStudentEmail(),
                studyList
        );
    }
}
