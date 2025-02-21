package com.onrank.server.api.controller;

import com.onrank.server.api.dto.oauth.CustomOAuth2User;
import com.onrank.server.api.dto.student.RegisterStudentDto;
import com.onrank.server.api.service.student.StudentService;
import com.onrank.server.common.util.CookieUtil;
import com.onrank.server.api.service.token.TokenService;
import com.onrank.server.domain.refreshtoken.RefreshTokenJpaRepository;
import com.onrank.server.domain.student.Student;
import com.onrank.server.domain.refreshtoken.RefreshToken;
import jakarta.servlet.http.Cookie;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.Optional;

@Slf4j
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final StudentService studentService;
    private final TokenService tokenService;
    private final RefreshTokenJpaRepository refreshTokenRepository;

    @PostMapping("/add")
    public ResponseEntity<Void> registerStudent(
            @RequestBody RegisterStudentDto registerStudentDto,
            @AuthenticationPrincipal CustomOAuth2User oAuth2User) {

        String username = oAuth2User.getName();
        String email = oAuth2User.getAttributes().get("email").toString();

        log.info("신규 회원 등록 - username: {}, email: {}", username, email);

        // Student 엔티티 생성 및 저장
        Student student = registerStudentDto.toEntity(username, email);
        studentService.createStudent(student);

        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    // (*) RTK 삭제 코드 작성해야 함
    @GetMapping("/refresh-token")
    public ResponseEntity<?> refreshToken(
            RequestEntity<Void> requestEntity,
            @AuthenticationPrincipal CustomOAuth2User oAuth2User) {

        String authorization = requestEntity.getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
        String refreshToken = requestEntity.getHeaders().getFirst("Refresh-Token");

        // AccessToken, RefreshToken 둘 다 없음
        if (authorization == null && refreshToken == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("AccessToken cookie not found.");
        }

        // AccessToken, RefreshToken 둘 중 하나만 있음 -> RTK 삭제 및 401 반환
        if (!(authorization != null && refreshToken != null)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("AccessToken cookie not found.");
        }

        String accessToken = authorization.substring(7);


        // AccessToken이 만료되지 않았는데 RefreshToken 재발급 요청을 보냄 -> RTK 삭제 및 401 반환
        if (!tokenService.isExpired(accessToken)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body("AccessToken not expired.");
        }

        if (tokenService.isExpired(refreshToken)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("Refresh token expired.");
        }

        // 액세스 토큰과 리프레시 토큰 생성
        String newAccessToken = tokenService.generateAccessToken(oAuth2User);
        String newRefreshToken = tokenService.generateRefreshToken(oAuth2User);

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + newAccessToken);
        headers.set("Refresh-Token", newRefreshToken);

        return ResponseEntity.status(HttpStatus.CREATED)
                .headers(headers)
                .build();
    }
}
