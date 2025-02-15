package com.onrank.server.api.service.auth;

import com.onrank.server.api.service.student.StudentService;
import com.onrank.server.common.security.jwt.JwtTokenProvider;
import com.onrank.server.domain.student.StudentRepository;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Slf4j
@Component
@RequiredArgsConstructor
public class OAuth2AuthenticationSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final StudentService studentService;
    private final JwtTokenProvider jwtTokenProvider;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {

        // OAuth2 인증된 사용자 정보 가져오기
        CustomOAuth2User oAuth2User = (CustomOAuth2User) authentication.getPrincipal();
        String email = oAuth2User.getEmail();

        log.info("OAuth2 로그인 성공 - 사용자 이메일: {}", email);

        // JWT 생성
        String token = jwtTokenProvider.generateToken(email);

        // JWT를 HTTP-Only 쿠키에 저장
        Cookie jwtCookie = new Cookie("Authorization", token);
        jwtCookie.setHttpOnly(true);
        jwtCookie.setSecure(false); // HTTPS 환경이 아니므로 false (운영에서는 true)
        jwtCookie.setPath("/");
        jwtCookie.setMaxAge(60 * 60 * 2); // 2시간 유지
        response.addCookie(jwtCookie);

        //JWT 응답 헤더에 추가
        response.setHeader("Authorization", "Bearer " + token);

        // 사용자가 이미 가입된 경우
        if (studentService.findByEmail(email).isPresent()) {
            log.info("기존 회원 - /studies 로 이동");
            response.setHeader("Location", "/studies");
        } else { // 신규 회원이면
            log.info("신규 회원 - /register-student 로 이동");
            response.setHeader("Location", "/register-student");
        }
    }
}
