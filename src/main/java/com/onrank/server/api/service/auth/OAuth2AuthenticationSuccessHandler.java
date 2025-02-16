package com.onrank.server.api.service.auth;

import com.onrank.server.api.dto.oauth.CustomOAuth2User;
import com.onrank.server.common.security.jwt.TokenProvider;
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

    private final TokenProvider tokenProvider;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException {

        // ✅ OAuth2 인증된 사용자 정보 가져오기
        CustomOAuth2User oAuth2User = (CustomOAuth2User) authentication.getPrincipal();
        String username = oAuth2User.getUsername();
        boolean isNewUser = oAuth2User.isNewUser();

        log.info("OAuth2 로그인 성공 - 사용자: {}", username);
        log.info("신규 회원 여부: {}", isNewUser);

        // AccessToken 생성 및 쿠키에 저장
        String accessToken = tokenProvider.generateAccessToken(username);
        Cookie accessTokenCookie = new Cookie("Authorization", accessToken);
        accessTokenCookie.setHttpOnly(true);
        accessTokenCookie.setSecure(request.isSecure()); // HTTPS 환경에서는 true
        accessTokenCookie.setPath("/");
        accessTokenCookie.setMaxAge(60 * 60 * 2); // 2시간 유지
        response.addCookie(accessTokenCookie);

        // ✅ 쿼리 파라미터를 추가하여 리다이렉트 (신규 회원 여부 포함)
        String redirectUrl = "http://localhost:3000/oauth?isNewUser=" + isNewUser;
        response.sendRedirect(redirectUrl);
    }
}
