package com.onrank.server.common.util;

import com.onrank.server.domain.refreshtoken.RefreshToken;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

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
    /**
     * Samesite = none 설정
     */
    public void addRefreshTokenCookie(HttpServletResponse response, String name, String refreshToken) {
        // 기존 쿠키 생성 코드
        Cookie refreshTokenCookie = new Cookie(name, refreshToken);
        refreshTokenCookie.setMaxAge(refreshTokenExpiration); // 2시간
        refreshTokenCookie.setPath("/");
        refreshTokenCookie.setHttpOnly(true);
        refreshTokenCookie.setSecure(true); // HTTPS에서만 전송되도록 변경

        // 도메인 설정 (도메인을 명시적으로 지정)
        refreshTokenCookie.setDomain("onrank.kr");

        // 기존 방식으로 쿠키 추가
        response.addCookie(refreshTokenCookie);

        // SameSite=None 설정을 위한 추가 헤더 설정 (Cookie API에서는 직접 설정 불가)
        String cookieHeader = response.getHeader("Set-Cookie");
        if (cookieHeader != null) {
            response.setHeader("Set-Cookie", cookieHeader + "; SameSite=None");
        } else {
            // Set-Cookie 헤더가 아직 없는 경우 (방어 코드)
            String cookieString = refreshTokenCookie.getName() + "=" + refreshTokenCookie.getValue() +
                    "; Path=" + refreshTokenCookie.getPath() +
                    "; Max-Age=" + refreshTokenCookie.getMaxAge() +
                    (refreshTokenCookie.isHttpOnly() ? "; HttpOnly" : "") +
                    (refreshTokenCookie.getSecure() ? "; Secure" : "") +
                    "; Domain=onrank.kr" +
                    "; SameSite=None";
            response.setHeader("Set-Cookie", cookieString);
        }

        // 디버깅용 로그 추가
        log.info("설정된 쿠키: {}", response.getHeader("Set-Cookie"));
    }
}
