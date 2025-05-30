package com.onrank.server.api.controller.auth;

import com.onrank.server.api.dto.student.AddStudentRequest;
import com.onrank.server.api.service.auth.AuthService;
import com.onrank.server.api.service.student.StudentService;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final StudentService studentService;
    private final AuthService authService;

    @PostMapping("/add")
    public ResponseEntity<Void> registerStudent(
            @RequestBody AddStudentRequest addStudentRequest,
            @RequestHeader("Authorization") String authorizationHeader) {

        studentService.createStudent(authorizationHeader, addStudentRequest);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @GetMapping("/reissue")
    public ResponseEntity<Void> reissueToken(
            @RequestHeader(name = "Authorization", required = false) String authorizationHeader,
            @CookieValue(name = "refresh_token", required = false) String refreshToken,
            HttpServletResponse response) {

        // Service 호출 : 내부에서 쿠키 세팅도 함께 처리됨
        String newAccessToken = authService.reissueTokens(authorizationHeader, refreshToken, response);

        // Response 에 Authorization 헤더 추가
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + newAccessToken);

        return ResponseEntity.ok()
                .headers(headers)
                .build();
    }

}