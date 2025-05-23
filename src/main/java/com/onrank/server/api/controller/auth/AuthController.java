package com.onrank.server.api.controller.auth;

import com.onrank.server.api.dto.student.AddStudentRequest;
import com.onrank.server.api.service.student.StudentService;
import com.onrank.server.common.util.JWTUtil;
import com.onrank.server.common.util.CookieUtil;
import com.onrank.server.domain.student.Role;
import com.onrank.server.domain.student.Student;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import java.util.HashSet;
import java.util.Set;

@Slf4j
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final StudentService studentService;
    private final JWTUtil JWTUtil;
    private final CookieUtil cookieUtil;

    @PostMapping("/add")
    public ResponseEntity<Void> registerStudent(
            @RequestBody AddStudentRequest addStudentRequest,
            @RequestHeader("Authorization") String authHeader) {

        String accessToken = authHeader.substring(7);
        String username = JWTUtil.getUsername(accessToken);
        String email = JWTUtil.getEmail(accessToken);

        Set<Role> roles = new HashSet<>();
        roles.add(Role.ROLE_USER);

        // Student 엔티티 생성 및 저장
        Student student = addStudentRequest.toEntity(username, email, roles);
        studentService.createStudent(student);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @GetMapping("/reissue")
    public ResponseEntity<?> reissueAccessToken(
            RequestEntity<Void> requestEntity,
            @CookieValue(name = "refresh_token", required = false) String refreshToken,
            HttpServletResponse response) {
        log.info("/reissue 진입");

        String authorizationHeader = requestEntity.getHeaders().getFirst(HttpHeaders.AUTHORIZATION);

        // 두 토큰 모두 전달되지 않은 경우
        if (authorizationHeader == null && refreshToken == null) {
            log.info("token 두 개 모두 전달 X");

            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("인증 정보가 제공되지 않았습니다. 로그인이 필요합니다.");
        }

        // access token만 전달된 경우
        if (authorizationHeader != null && refreshToken == null) {
            log.info("access token만 전달");

            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("토큰 갱신 요청에 refresh token이 누락되었습니다.");
        }

        // refresh token만 전달된 경우
        if (authorizationHeader == null) {

            // refresh token이 만료된 경우
            if (JWTUtil.isTokenExpired(refreshToken)) {
                log.info("refresh만 전달, refreshToken 만료");

                JWTUtil.deleteRefreshToken(refreshToken);
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body("만료된 refresh token입니다. 다시 로그인해주세요.");
            }

            // refresh token이 유효하지 않은 경우
            if (!JWTUtil.isTokenValid(refreshToken)) {
                log.info("refresh만 전달, refreshToken 유효하지 않음");

                JWTUtil.deleteRefreshToken(refreshToken);
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body("유효하지 않은 refresh token입니다. 다시 로그인해주세요.");
            }

            // refresh token이 유효한 경우 -> 새로운 토큰 발급
            return getResponseEntity(refreshToken, response);
        }

        log.info("authorizationHeader: {}", authorizationHeader);

        // access token이 올바르지 않은 경우
        if (!authorizationHeader.startsWith("Bearer ")) {

            log.info("authorizationHeader: {}", authorizationHeader);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("인증 정보가 제공되지 않았습니다. 로그인이 필요합니다.");
        }
        String accessToken = authorizationHeader.substring(7);


        // access token이 만료되지 않은 경우 -> 보안상의 이유로 재인증 처리
        if (!JWTUtil.isTokenExpired(accessToken)) {
            log.info("access token expired");

            JWTUtil.deleteRefreshToken(refreshToken);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("만료되지 않은 access token과 함께 refresh token이 전달되었습니다. 보안상의 이유로 재인증이 필요합니다.");
        }

        // refresh token이 만료된 경우
        if (JWTUtil.isTokenExpired(refreshToken)) {
            log.info("access 만료, refreshToken 유효하지 않음");

            JWTUtil.deleteRefreshToken(refreshToken);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("만료된 refresh token입니다. 다시 로그인해주세요.");
        }

        // refresh token이 유효하지 않은 경우
        if (!JWTUtil.isTokenValid(refreshToken)) {
            log.info("access 만료, refreshToken 유효하지 않음");

            JWTUtil.deleteRefreshToken(refreshToken);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("유효하지 않은 refresh token입니다. 다시 로그인해주세요.");
        }

        // refresh token이 유효한 경우 -> 새로운 토큰 발급
        return getResponseEntity(refreshToken, response);
    }

    private ResponseEntity<?> getResponseEntity(@CookieValue(name = "refresh_token", required = false) String refreshToken, HttpServletResponse response) {
        String username = JWTUtil.getUsername(refreshToken);
        String email = JWTUtil.getEmail(refreshToken);

        String newAccessToken = JWTUtil.createJwt("access", username, email);
        String newRefreshToken = JWTUtil.createJwt("refresh", username, email);

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + newAccessToken);

        cookieUtil.addRefreshTokenCookie(response, "refresh_token", newRefreshToken);

        // 기존 refresh token 삭제 후 새 refresh token 저장
        JWTUtil.deleteRefreshToken(refreshToken);
        JWTUtil.save(username, newRefreshToken);

        return ResponseEntity.ok()
                .headers(headers)
                .build();
    }
}