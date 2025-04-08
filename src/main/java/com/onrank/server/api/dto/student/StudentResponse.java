package com.onrank.server.api.dto.student;

import com.onrank.server.domain.student.Student;
import lombok.Getter;

@Getter
public class StudentResponse {

    private Long studentId;
    private String studentName;
    private String studentSchool;
    private String studentDepartment;
    private String studentPhoneNumber;
    private String studentEmail;

    public StudentResponse(Student student) {
        this.studentId = student.getStudentId();
        this.studentName = student.getStudentName();
        this.studentSchool = student.getStudentSchool();
        this.studentDepartment = student.getStudentDepartment();
        this.studentPhoneNumber = student.getStudentPhoneNumber();
        this.studentEmail = student.getStudentEmail();
    }
}
