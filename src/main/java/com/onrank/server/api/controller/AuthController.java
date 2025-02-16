package com.onrank.server.api.controller;

import com.onrank.server.api.dto.oauth.CustomOAuth2User;
import com.onrank.server.common.security.jwt.TokenProvider;
import com.onrank.server.api.service.student.StudentService;
import com.onrank.server.domain.student.RegisterStudentDto;
import com.onrank.server.domain.student.Student;
import com.onrank.server.domain.token.RefreshToken;
import com.onrank.server.domain.token.RefreshTokenRepository;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@RestController
@RequiredArgsConstructor
public class AuthController {

    private final StudentService studentService;
    private final TokenProvider tokenProvider;
    private final RefreshTokenRepository refreshTokenRepository;

    /**
     * 🔹 OAuth2 로그인 후 현재 사용자 정보 반환
     * - 프론트엔드에서 `/auth/me` 호출 후 적절한 페이지로 이동 (리다이렉트 X)
     */
    @GetMapping("/oauth/me")
    public ResponseEntity<Map<String, Object>> getCurrentUser(HttpServletRequest request) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !(authentication.getPrincipal() instanceof CustomOAuth2User)) {
            return ResponseEntity.ok(Map.of("authenticated", false));
        }

        CustomOAuth2User user = (CustomOAuth2User) authentication.getPrincipal();
        String email = user.getAttribute("email");

        log.info("✅ 로그인된 사용자: {}, isNewUser: {}", email, user.isNewUser());

        return ResponseEntity.ok(Map.of(
                "authenticated", true,
                "isNewUser", user.isNewUser(),
                "email", email
        ));
    }


    /**
     * 🔹 `register-student` 페이지 렌더링
     * - JWT 기반으로 인증된 사용자 확인
     * - AccessToken에서 이메일 정보 추출
     */
    @GetMapping("/register-student")
    public Map<String, Object> registerStudentForm(HttpServletRequest request) {
        String email = getEmailFromToken(request);
        if (email == null) {
            log.warn("❌ 유효한 AccessToken이 없음 - 로그인 필요");
            return Map.of("error", "Unauthorized");
        }

        return Map.of("email", email);
    }

    /**
     * 🔹 회원가입 요청 처리
     * - 추가 정보를 입력받아 회원 저장
     * - 새로운 AccessToken & RefreshToken 발급
     */
    @PostMapping("/register-student")
    public Map<String, String> registerStudent(@RequestBody RegisterStudentDto request, HttpServletRequest httpRequest, HttpServletResponse response) throws IOException {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !(authentication.getPrincipal() instanceof CustomOAuth2User)) {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Unauthorized");
            return Map.of();
        }

        CustomOAuth2User user = (CustomOAuth2User) authentication.getPrincipal();
        String email = user.getEmail();

        Student student = request.toEntity(email);
        studentService.createMember(student);

        String accessToken = tokenProvider.generateAccessToken(email);
        String refreshToken = UUID.randomUUID().toString();

        refreshTokenRepository.save(new RefreshToken(email, refreshToken));

        return Map.of("accessToken", accessToken, "refreshToken", refreshToken);
    }


    /**
     * 🔹 AccessToken & RefreshToken을 쿠키에 저장
     */
    private void setTokenCookies(HttpServletResponse response, String accessToken, String refreshToken) {
        Cookie accessTokenCookie = new Cookie("AccessToken", accessToken);
        accessTokenCookie.setHttpOnly(true);
        accessTokenCookie.setSecure(false);
        accessTokenCookie.setPath("/");
        accessTokenCookie.setMaxAge(60 * 60 * 2);

        Cookie refreshTokenCookie = new Cookie("RefreshToken", refreshToken);
        refreshTokenCookie.setHttpOnly(true);
        refreshTokenCookie.setSecure(false);
        refreshTokenCookie.setPath("/");
        refreshTokenCookie.setMaxAge(60 * 60 * 24 * 30);

        response.addCookie(accessTokenCookie);
        response.addCookie(refreshTokenCookie);
    }

    /**
     * 🔹 RefreshToken을 이용한 AccessToken 재발급
     */
    @PostMapping("/refresh-token")
    public Map<String, Object> refreshToken(HttpServletRequest request, HttpServletResponse response) {
        String refreshToken = null;

        // RefreshToken 쿠키에서 가져오기
        for (Cookie cookie : request.getCookies()) {
            if ("RefreshToken".equals(cookie.getName())) {
                refreshToken = cookie.getValue();
            }
        }

        if (refreshToken == null) {
            return Map.of("error", "Unauthorized");
        }

        Optional<RefreshToken> storedToken = refreshTokenRepository.findByRefreshToken(refreshToken);
        if (storedToken.isEmpty()) {
            return Map.of("error", "Unauthorized");
        }

        // 새로운 AccessToken 생성
        String newAccessToken = tokenProvider.generateAccessToken(storedToken.get().getEmail());

        // 새로운 AccessToken을 쿠키에 저장
        setTokenCookies(response, newAccessToken, refreshToken);

        return Map.of("success", true);
    }
}
