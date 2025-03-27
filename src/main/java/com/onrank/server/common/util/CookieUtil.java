package com.onrank.server.common.util;

import com.onrank.server.domain.refreshtoken.RefreshToken;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.Arrays;
import java.util.Optional;

@Slf4j
@Component
public class CookieUtil {

    private final int refreshTokenExpiration;
    private final String refreshTokenPath;

    public CookieUtil(@Value("${jwt.refresh.expirationSec}")int refreshTokenExpiration,
                      @Value("${jwt.refresh.path}") String refreshTokenPath) {
        this.refreshTokenExpiration = refreshTokenExpiration;
        this.refreshTokenPath = refreshTokenPath;
    }

    /**
     * refresh token 쿠키 생성 및 응답에 추가
     */
//    public void addRefreshTokenCookie(HttpServletResponse response, String name, String refreshToken) {
//        Cookie refreshTokenCookie = new Cookie(name, refreshToken);
//        refreshTokenCookie.setMaxAge(refreshTokenExpiration); // 2시간
//        refreshTokenCookie.setPath(refreshTokenPath);
//        refreshTokenCookie.setPath("/");
//        refreshTokenCookie.setHttpOnly(true);
//        refreshTokenCookie.setSecure(false);
//
//        response.addCookie(refreshTokenCookie);
//    }
    public void addRefreshTokenCookie(HttpServletResponse response, String name, String refreshToken) {
        ResponseCookie cookie = ResponseCookie.from(name, refreshToken)
                .httpOnly(true)
                .secure(true)  // HTTPS에서만 전송
                .path("/")
                .maxAge(Duration.ofSeconds(refreshTokenExpiration))
                .domain("onrank.kr")
                .sameSite("None")  // 크로스 사이트 요청 허용
                .build();

        response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());

        // 디버깅용 로그 추가
        log.info("설정된 쿠키: {}", cookie);
    }
}
