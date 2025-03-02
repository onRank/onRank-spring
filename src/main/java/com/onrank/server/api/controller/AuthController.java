package com.onrank.server.api.controller;

import com.onrank.server.api.dto.oauth.CustomOAuth2User;
import com.onrank.server.api.dto.student.RegisterStudentDto;
import com.onrank.server.api.service.student.StudentService;
import com.onrank.server.api.service.token.TokenService;
import com.onrank.server.common.util.CookieUtil;
import com.onrank.server.domain.student.Student;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final StudentService studentService;
    private final TokenService tokenService;
    private final CookieUtil cookieUtil;

    @PostMapping("/add")
    public ResponseEntity<Void> registerStudent(
            @RequestBody RegisterStudentDto registerStudentDto,
            @RequestHeader("Authorization") String authHeader) {

        String accessToken = authHeader.substring(7);



        String username = tokenService.getUsername(accessToken);
        String email = tokenService.getEmail(accessToken);

        // Student 엔티티 생성 및 저장
        Student student = registerStudentDto.toEntity(username, email);

        log.info("신규 회원 등록 - username: {}, email: {}, studentName: {}, studentPhoneNumber: {}, studentSchool: {}, studentDepartment: {}",
                username, email, student.getStudentName(), student.getStudentPhoneNumber(), student.getStudentSchool(), student.getStudentDepartment());

        studentService.createStudent(student);

        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @GetMapping("/reissue")
    public ResponseEntity<?> refreshToken(
            RequestEntity<Void> requestEntity,
            @CookieValue(name = "refresh_token", required = false) String refreshToken,
            HttpServletResponse response) {
        log.info("/reissue 진입");

        String authorizationHeader = requestEntity.getHeaders().getFirst(HttpHeaders.AUTHORIZATION);

        // 두 토큰 모두 전달되지 않은 경우
        if (authorizationHeader == null && refreshToken == null) {

            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("인증 정보가 제공되지 않았습니다. 로그인이 필요합니다.");
        }

        // access token만 전달된 경우
        if (authorizationHeader != null && refreshToken == null) {

            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("토큰 갱신 요청에 refresh token이 누락되었습니다.");
        }

        // refresh token만 전달된 경우
        if (authorizationHeader == null) {

            // refresh token이 유효하지 않은 경우
            if (!tokenService.validateRefreshToken(refreshToken)) {

                tokenService.deleteRefreshToken(refreshToken);
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body("유효하지 않은 refresh token입니다. 다시 로그인해주세요.");
            }
            // refresh token이 유효한 경우 -> 새로운 토큰 발급
            return getResponseEntity(refreshToken, response);
        }

        // access token이 올바르지 않은 경우
        if (!authorizationHeader.startsWith("Bearer ")) {

            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("인증 정보가 제공되지 않았습니다. 로그인이 필요합니다.");
        }
        String accessToken = authorizationHeader.substring(7);


        // access token이 만료되지 않은 경우 -> 보안상의 이유로 재인증 처리
        if (!tokenService.isExpired(accessToken)) {

            tokenService.deleteRefreshToken(refreshToken);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("만료되지 않은 access token과 함께 refresh token이 전달되었습니다. 보안상의 이유로 재인증이 필요합니다.");
        }

        // refresh token이 만료되었거나 유효하지 않을 경우
        if (tokenService.isExpired(refreshToken)) {

            tokenService.deleteRefreshToken(refreshToken);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("만료되지 않은 refresh token입니다. 다시 로그인해주세요.");
        }

        // refresh token이 유효한 경우 -> 새로운 토큰 발급
        return getResponseEntity(refreshToken, response);
    }

    private ResponseEntity<?> getResponseEntity(@CookieValue(name = "refresh_token", required = false) String refreshToken, HttpServletResponse response) {
        String username = tokenService.getUsername(refreshToken);
        String email = tokenService.getEmail(refreshToken);

        String newAccessToken = tokenService.createJwt("access", username, email);
        String newRefreshToken = tokenService.createJwt("refresh", username, email);

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + newAccessToken);

        cookieUtil.addRefreshTokenCookie(response, "refresh_token", newRefreshToken);

        // 기존 refresh token 삭제 후 새 refresh token 저장
        tokenService.deleteRefreshToken(refreshToken);
        tokenService.save(username, newRefreshToken);

        return ResponseEntity.ok()
                .headers(headers)
                .build();
    }
}
