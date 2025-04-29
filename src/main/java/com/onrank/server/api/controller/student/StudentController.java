package com.onrank.server.api.controller.student;

import com.onrank.server.api.dto.oauth.CustomOAuth2User;
import com.onrank.server.api.dto.student.AddStudentRequest;
import com.onrank.server.api.dto.student.StudentResponse;
import com.onrank.server.api.service.student.StudentService;
import com.onrank.server.domain.student.Student;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/auth/{studentId}")
@RequiredArgsConstructor
public class StudentController {

    private final StudentService studentService;

    @GetMapping
    public ResponseEntity<StudentResponse> getStudent(
            @PathVariable Long studentId,
            @AuthenticationPrincipal CustomOAuth2User oAuth2User) {

        Student student = studentService.findByStudentId(studentId)
                .orElseThrow(() -> new IllegalArgumentException("Student not found"));

        // 본인만 조회 가능
        if (!student.getUsername().equals(oAuth2User.getName())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        return ResponseEntity.ok(new StudentResponse(student));
    }

    @PutMapping
    public ResponseEntity<Void> updateStudent(
            @PathVariable Long studentId,
            @RequestBody AddStudentRequest addStudentRequest,
            @AuthenticationPrincipal CustomOAuth2User oAuth2User) {

        Student student = studentService.findByStudentId(studentId)
                .orElseThrow(() -> new IllegalArgumentException("Student not found"));

        // 본인만 조회 가능
        if (!student.getUsername().equals(oAuth2User.getName())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        studentService.updateStudent(studentId, addStudentRequest);
        return ResponseEntity.ok().build();
    }
}
