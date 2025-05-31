package com.onrank.server.api.service.auth;

import com.onrank.server.common.exception.CustomException;
import com.onrank.server.common.util.CookieUtil;
import com.onrank.server.common.util.JWTUtil;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import static com.onrank.server.common.exception.CustomErrorInfo.*;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final JWTUtil jwtUtil;
    private final CookieUtil cookieUtil;

    public String reissueTokens(String authorizationHeader, String refreshToken, HttpServletResponse response) {
        // 1) 둘다 모두 전달되지 않은 경우 (예외)
        if (authorizationHeader == null && refreshToken == null) {
            throw new CustomException(LOGIN_REQUIRED);
        }

        // 2) access token 만 전달된 경우 (예외)
        if (authorizationHeader != null && refreshToken == null) {
            throw new CustomException(REFRESH_TOKEN_IS_NULL);
        }

        // 3) refresh token 만 전달된 경우 -> 토큰 검증 후 발급
        if (authorizationHeader == null) {
            jwtUtil.validateRefreshToken(refreshToken);
            return issueNewTokens(refreshToken, response);
        }

        // 4) Authorization 헤더 형식 검사
        if (!authorizationHeader.startsWith("Bearer ")) {
            throw new CustomException(INVALID_ACCESS_TOKEN);
        }
        String accessToken = authorizationHeader.substring(7);


        // 5) access token 검증 : 만료시 validateAccessToken 에서 ACCESS_TOKEN_EXPIRED 예외 발생
        try {
            jwtUtil.validateAccessToken(accessToken);
            // validateAccessToken 통과하면 아직 만료되지 않은 상태이므로 보안상 재인증 필요
            jwtUtil.deleteRefreshToken(refreshToken);
            throw new CustomException(RE_AUTHENTICATION_REQUIRED);
        } catch (CustomException ex) {
            if(ex.getCustomErrorInfo() != ACCESS_TOKEN_EXPIRED) {
                // AccessToken 만료 외의 다른 오류일시, 그대로 던짐
                throw ex;
            }
            // ACCESS_TOKEN_EXPIRED 인 경우, refreshToken 검증으로 넘어감
        }

        // 6) refreshToken 검증 : 유효한 경우 -> 새로운 토큰 발급
        jwtUtil.validateRefreshToken(refreshToken);
        return issueNewTokens(refreshToken, response);
    }

    private String issueNewTokens(String oldRefreshToken, HttpServletResponse response) {
        String username = jwtUtil.getUsername(oldRefreshToken);
        String email = jwtUtil.getEmail(oldRefreshToken);

        String newAccessToken = jwtUtil.createJwt("access", username, email);
        String newRefreshToken = jwtUtil.createJwt("refresh", username, email);

        cookieUtil.addRefreshTokenCookie(response, "refresh_token", newRefreshToken);
        jwtUtil.deleteRefreshToken(oldRefreshToken);
        jwtUtil.save(username, newRefreshToken);

        return newAccessToken;
    }
}
