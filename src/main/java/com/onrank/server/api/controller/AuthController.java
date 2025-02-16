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
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final StudentService studentService;
    private final TokenProvider tokenProvider;
    private final RefreshTokenRepository refreshTokenRepository;

    @PostMapping("/add")
    public Map<String, String> registerStudent(@RequestBody RegisterStudentDto request, HttpServletResponse response) throws IOException {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !(authentication.getPrincipal() instanceof CustomOAuth2User)) {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Unauthorized");
            return Map.of();
        }

        CustomOAuth2User user = (CustomOAuth2User) authentication.getPrincipal();
        String username = user.getUsername();
        String email = user.getAttributes().get("email").toString();

        log.info("신규 회원 등록 - username: {}, email: {}", username, email);

        // Student 엔티티 생성 시 username과 email을 함께 저장
        Student student = request.toEntity(username, email);
        studentService.createStudent(student);

        // AccessToken 및 RefreshToken 생성
        String accessToken = tokenProvider.generateAccessToken(username);
        String refreshToken = UUID.randomUUID().toString();

        // RefreshToken 저장 (username을 기준으로 저장)
        refreshTokenRepository.save(new RefreshToken(username, refreshToken));

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
