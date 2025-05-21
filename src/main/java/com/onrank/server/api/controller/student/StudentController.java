package com.onrank.server.api.controller.student;

import com.onrank.server.api.dto.oauth.CustomOAuth2User;
import com.onrank.server.api.dto.student.AddStudentRequest;
import com.onrank.server.api.dto.student.CalendarResponse;
import com.onrank.server.api.dto.student.StudentResponse;
import com.onrank.server.api.service.student.StudentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class StudentController implements StudentControllerDocs {

    private final StudentService studentService;

    // 마이페이지
    @GetMapping("/mypage")
    public ResponseEntity<StudentResponse> getStudent(
            @AuthenticationPrincipal CustomOAuth2User oAuth2User) {
        return ResponseEntity.ok(studentService.getMyPage(oAuth2User.getName()));
    }

    @PutMapping("/{studentId}")
    public ResponseEntity<Void> updateStudent(
            @PathVariable Long studentId,
            @RequestBody AddStudentRequest addStudentRequest,
            @AuthenticationPrincipal CustomOAuth2User oAuth2User) {
        studentService.updateStudent(oAuth2User.getName(), studentId, addStudentRequest);
        return ResponseEntity.ok().build();
    }

    @GetMapping("calendar")
    public ResponseEntity<List<CalendarResponse>> getCalendar (
            @AuthenticationPrincipal CustomOAuth2User oAuth2User) {
        return ResponseEntity.ok(studentService.getCalendar(oAuth2User.getName()));
    }
}
