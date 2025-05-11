package com.onrank.server.api.controller.student;

import com.onrank.server.api.dto.oauth.CustomOAuth2User;
import com.onrank.server.api.dto.student.AddStudentRequest;
import com.onrank.server.api.dto.student.StudentResponse;
import com.onrank.server.api.service.student.StudentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/auth/{studentId}")
@RequiredArgsConstructor
public class StudentController implements StudentControllerDocs {

    private final StudentService studentService;

    // 마이페이지
    @GetMapping
    public ResponseEntity<StudentResponse> getStudent(
            @PathVariable Long studentId,
            @AuthenticationPrincipal CustomOAuth2User oAuth2User) {
        return ResponseEntity.ok(studentService.getMyPage(oAuth2User.getName(), studentId));
    }

    @PutMapping
    public ResponseEntity<Void> updateStudent(
            @PathVariable Long studentId,
            @RequestBody AddStudentRequest addStudentRequest,
            @AuthenticationPrincipal CustomOAuth2User oAuth2User) {
        studentService.updateStudent(oAuth2User.getName(), studentId, addStudentRequest);
        return ResponseEntity.ok().build();
    }
}
