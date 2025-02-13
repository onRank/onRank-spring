package com.onrank.server.api.service.auth;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

public class OAuth2AuthenticationSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {

        // OAuth2 인증된 사용자 정보 가져오기
        CustomOAuth2User oAuth2User = (CustomOAuth2User) authentication.getPrincipal();
        String email = oAuth2User.getEmail();

        if (oAuth2User.isNewUser()) {
            // 신규 회원이면 기존 정보 입력 폼으로 이동
            String redirectUrl = "/register-student?email=" + URLEncoder.encode(email, StandardCharsets.UTF_8);
            response.sendRedirect("/");
        } else {
            // 기존 회원이면 메인 페이지로 이동
            response.sendRedirect("/");
        }
    }
}
