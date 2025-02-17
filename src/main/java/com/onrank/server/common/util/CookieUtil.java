package com.onrank.server.common.util;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.Arrays;
import java.util.Optional;

public class CookieUtil {

    private static final int ACCESS_TOKEN_EXPIRY = 60 * 60 * 2; // 2시간
    private static final int REFRESH_TOKEN_EXPIRY = 60 * 60 * 24 * 30; // 30일

    /**
     * 쿠키 생성 및 응답에 추가
     */
    public static void addCookie(HttpServletResponse response, String name, String value, int maxAge) {
        Cookie cookie = new Cookie(name, value);
        cookie.setHttpOnly(true);
        cookie.setSecure(false); // HTTPS 환경이면 true로 변경
        cookie.setPath("/");
        cookie.setMaxAge(maxAge);

        response.addCookie(cookie);
    }

    /**
     * AccessToken & RefreshToken 쿠키 설정
     */
    public static void setAuthCookies(HttpServletResponse response, String accessToken, String refreshToken) {
        addCookie(response, "AccessToken", accessToken, ACCESS_TOKEN_EXPIRY);
        addCookie(response, "RefreshToken", refreshToken, REFRESH_TOKEN_EXPIRY);
    }

    /**
     * 요청에서 특정 쿠키 값을 가져오기
     */
    public static Optional<String> getCookieValue(HttpServletRequest request, String name) {
        if (request.getCookies() == null) {
            return Optional.empty();
        }
        return Arrays.stream(request.getCookies())
                .filter(cookie -> name.equals(cookie.getName()))
                .map(Cookie::getValue)
                .findFirst();
    }
}
