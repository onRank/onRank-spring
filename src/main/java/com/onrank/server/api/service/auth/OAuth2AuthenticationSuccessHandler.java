package com.onrank.server.api.service.auth;

import com.onrank.server.api.service.refreshtoken.RefreshTokenService;
import com.onrank.server.common.util.CookieUtil;
import com.onrank.server.common.util.JWTUtil;
import com.onrank.server.domain.student.StudentJpaRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.*;

@Slf4j
@Component
@RequiredArgsConstructor
public class OAuth2AuthenticationSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final JWTUtil jwtUtil;
    private final RefreshTokenService refreshTokenService;
    private final StudentJpaRepository studentJpaRepository;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException {

        log.info("OAuth2 로그인 성공");

//        // OAuth2 인증된 사용자 정보 가져오기
//        CustomOAuth2User oAuth2User = (CustomOAuth2User) authentication.getPrincipal();
//        String username = oAuth2User.getUsername();

        DefaultOAuth2User defaultOAuth2User = (DefaultOAuth2User) authentication.getPrincipal();

        Collection<? extends GrantedAuthority> authorities = defaultOAuth2User.getAuthorities();
        log.info("authorities: {}", authorities);

        Map<String, Object> attributes = defaultOAuth2User.getAttributes();
        log.info("attributes: {}", attributes);

        String username = defaultOAuth2User.getName();
        log.info("username : {}", username);

        // authorities는 "ROLE_USER"로 고정
        String fixedAuthority = "ROLE_USER";

        // attributes에서 "sub" 값을 추출
        Object subValue = defaultOAuth2User.getAttributes().get("sub");
        if (subValue == null) {
            throw new IllegalArgumentException("OAuth2 사용자 정보에 'sub' 값이 없습니다.");
        }

        // JWT 클레임 구성: "sub"와 고정된 authorities 값을 넣음
        Map<String, Object> claims = new HashMap<>();
        claims.put("sub", subValue);
        claims.put("authorities", Collections.singletonList(fixedAuthority));

        // AccessToken 발급
        String accessToken = jwtUtil.generateAccessToken(claims);
        log.info("accessToken 발급");

        String refreshToken = UUID.randomUUID().toString();
        log.info("refreshToken 발급");

        // RefreshToken DB에 저장
        refreshTokenService.createRefreshToken(username, refreshToken);
        log.info("RefreshToken DB에 저장");

        // 쿠키 설정
        CookieUtil.setAuthCookies(response, accessToken, refreshToken);

        boolean isNewUser = studentJpaRepository.existsByUsername(username);
        isNewUser = !isNewUser;
        log.info("isNewUser: {}", isNewUser);

        // 쿼리 파라미터를 추가하여 리다이렉트 (신규 회원 여부 포함)
        String redirectUrl = "http://localhost:3000/auth/callback?isNewUser=" + isNewUser;
        response.sendRedirect(redirectUrl);
        log.info("redirectUrl: {}", redirectUrl);
    }
}
