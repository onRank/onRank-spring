package com.onrank.server.api.service.auth;

import com.onrank.server.api.dto.oauth.CustomOAuth2User;
import com.onrank.server.api.service.student.StudentService;
import com.onrank.server.common.util.JWTUtil;
import com.onrank.server.common.util.CookieUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Slf4j
@Component
public class OAuth2AuthenticationSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final JWTUtil JWTUtil;
    private final StudentService studentService;
    private final CookieUtil cookieUtil;
    @Value("${app.oauth.redirect-base-url}")
    private String redirectBaseUrl;

    public OAuth2AuthenticationSuccessHandler(JWTUtil JWTUtil,
                                                StudentService studentService,
                                                CookieUtil cookieUtil) {
        this.JWTUtil = JWTUtil;
        this.studentService = studentService;
        this.cookieUtil = cookieUtil;
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
                                        HttpServletResponse response,
                                        Authentication authentication)
            throws IOException {

        log.info("OAuth2 로그인 성공");

        // 사용자 정보
        CustomOAuth2User customOAuth2User = (CustomOAuth2User) authentication.getPrincipal();
        String username = customOAuth2User.getUsername();
        String email = customOAuth2User.getEmail();

        // 리프레시 토큰 생성
        String refreshToken = JWTUtil.createJwt("refresh", username, email);

        // refresh token DB에 저장
        JWTUtil.save(username, refreshToken);

        // refresh token 쿠키 생성 및 응답에 추가
        cookieUtil.addRefreshTokenCookie(response, "refresh_token", refreshToken);

        // 신규 회원 여부 확인
        boolean isNewUser = studentService.checkIfNewUser(authentication.getName());

        // 리다이렉트 URL 구성 후 sendRedirect 호출
        String redirectUrl = redirectBaseUrl + "?isNewUser=" + isNewUser;

        response.sendRedirect(redirectUrl);
    }
}