package com.onrank.server.api.service.auth;

import com.onrank.server.api.dto.oauth.CustomOAuth2User;
import com.onrank.server.api.service.student.StudentService;
import com.onrank.server.api.service.token.TokenService;
import com.onrank.server.domain.student.StudentJpaRepository;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.*;

@Slf4j
@Component
@RequiredArgsConstructor
public class OAuth2AuthenticationSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final TokenService tokenService;
    private final StudentService studentService;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
                                        HttpServletResponse response,
                                        Authentication authentication)
            throws ServletException, IOException {

        log.info("OAuth2 로그인 성공");

        CustomOAuth2User customOAuth2User = (CustomOAuth2User) authentication.getPrincipal();

        // 액세스 토큰과 리프레시 토큰 생성
        String accessToken = tokenService.generateAccessToken(customOAuth2User);
        String refreshToken = tokenService.generateRefreshToken(customOAuth2User);

        // 응답 헤더에 토큰 추가
        response.setHeader("Authorization", "Bearer " + accessToken);
        response.setHeader("Refresh-Token", refreshToken);

        // 신규 회원 여부 확인
        boolean isNewUser = studentService.checkIfNewUser(authentication.getName());

        // 리다이렉트 URL 구성 후 sendRedirect 호출
        String redirectUrl = "http://localhost:3000/auth/callback?isNewUser=" + isNewUser;
        response.sendRedirect(redirectUrl);
    }
}
