package com.onrank.server.api.controller;

import com.onrank.server.api.service.refreshtoken.RefreshTokenService;
import com.onrank.server.api.service.student.StudentService;
import com.onrank.server.common.util.CookieUtil;
import com.onrank.server.common.util.JWTUtil;
import com.onrank.server.domain.student.RegisterStudentDto;
import com.onrank.server.domain.student.Student;
import com.onrank.server.domain.refreshtoken.RefreshToken;
import com.onrank.server.domain.refreshtoken.RefreshTokenRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.Map;
import java.util.Optional;

@Slf4j
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final StudentService studentService;
    private final RefreshTokenService refreshTokenService;
    private final JWTUtil jwtUtil;
    private final RefreshTokenRepository refreshTokenRepository;

    // ✅ 프론트엔드에서 이 API를 호출하면 Google 로그인 페이지로 리디렉션
    @GetMapping("/login")
    public void redirectToGoogle(HttpServletResponse response) throws IOException {
        response.sendRedirect("/oauth2/authorization/google"); // Spring Security가 자동 처리
    }

    @PostMapping("/add")
    public Map<String, String> registerStudent(@RequestBody RegisterStudentDto request, HttpServletResponse response) throws IOException {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !(authentication.getPrincipal() instanceof DefaultOAuth2User user)) {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Unauthorized");
            return Map.of();
        }

        String username = user.getName();
        String email = user.getAttributes().get("email").toString();

        log.info("신규 회원 등록 - username: {}, email: {}", username, email);

        // Student 엔티티 생성 및 저장
        Student student = request.toEntity(username, email);
        studentService.createStudent(student);

        // return 값 설정 필요
        return Map.of();
    }

    @PostMapping("/refresh-token")
    public Map<String, Object> refreshToken(HttpServletRequest request, HttpServletResponse response) {
        Optional<String> refreshTokenOpt = CookieUtil.getCookieValue(request, "RefreshToken");

        if (refreshTokenOpt.isEmpty()) {
            return Map.of("error", "Unauthorized");
        }

        String refreshToken = refreshTokenOpt.get();
        Optional<RefreshToken> storedToken = refreshTokenRepository.findByRefreshToken(refreshToken);
        if (storedToken.isEmpty()) {
            return Map.of("error", "Unauthorized");
        }

        // 새로운 AccessToken 생성
        String newAccessToken = jwtUtil.generateAccessToken(storedToken.get().getUsername());

        // 새로운 AccessToken을 쿠키에 저장
        CookieUtil.setAuthCookies(response, newAccessToken, refreshToken);

        return Map.of("success", true);
    }
}
