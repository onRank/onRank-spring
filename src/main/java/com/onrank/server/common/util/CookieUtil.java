package com.onrank.server.common.util;

import com.onrank.server.domain.refreshtoken.RefreshToken;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Optional;

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
    public void addRefreshTokenCookie(HttpServletResponse response, String name, String refreshToken) {
        Cookie refreshTokenCookie = new Cookie(name, refreshToken);
        refreshTokenCookie.setMaxAge(refreshTokenExpiration); // 2시간
        refreshTokenCookie.setPath(refreshTokenPath);
        refreshTokenCookie.setPath("/");
        refreshTokenCookie.setHttpOnly(true);
        refreshTokenCookie.setSecure(false);

        response.addCookie(refreshTokenCookie);
    }
}
