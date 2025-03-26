package com.onrank.server.api.controller;

import com.onrank.server.api.dto.error.ErrorResponse;
import com.onrank.server.api.dto.oauth.CustomOAuth2User;
import com.onrank.server.api.dto.student.RegisterStudentDto;
import com.onrank.server.api.exception.UnAuthorizedException;
import com.onrank.server.api.service.student.StudentService;
import com.onrank.server.common.util.JWTUtil;
import com.onrank.server.common.util.CookieUtil;
import com.onrank.server.domain.student.Role;
import com.onrank.server.domain.student.Student;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.headers.Header;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.HashSet;
import java.util.Set;

@Slf4j
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final StudentService studentService;
    private final JWTUtil JWTUtil;
    private final CookieUtil cookieUtil;

    @Operation(
            summary = "회원 등록",
            description = "OAuth2 인증 이후 사용자 정보를 등록합니다.",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "회원 등록 요청 데이터",
                    required = true,
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = RegisterStudentDto.class)
                    )
            )
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "201",
                    description = "회원 등록 성공",
                    headers = @Header(
                            name = "Location",
                            description = "생성된 회원 리소스의 URI",
                            schema = @Schema(type = "string", example = "/students/1")
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "요청 데이터 오류",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "서버 내부 오류",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class)
                    )
            )
    })
    @PostMapping("/add")
    public ResponseEntity<Void> registerStudent(
            @Valid @RequestBody RegisterStudentDto registerStudentDto,
            @AuthenticationPrincipal CustomOAuth2User oAuth2User) {

        String username = oAuth2User.getName();
        String email = oAuth2User.getEmail();

        Set<Role> roles = new HashSet<>();
        roles.add(Role.ROLE_USER);

        Student student = registerStudentDto.toEntity(username, email, roles);

        log.info("신규 회원 등록 - username: {}, email: {}, studentName: {}", username, email, student.getStudentName());

        studentService.createStudent(student);

        URI studentURI = URI.create("/students/" + student.getStudentId());
        return ResponseEntity.created(studentURI).build();
    }

    @Operation(summary = "Access Token 재발급", description = "Refresh Token을 사용해 Access Token을 재발급합니다.")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "토큰 재발급 성공"
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "토큰 누락 또는 유효하지 않음",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            )
    })
    @GetMapping("/reissue")
    public ResponseEntity<?> refreshToken(
            RequestEntity<Void> requestEntity,
            @CookieValue(name = "refresh_token", required = false) String refreshToken,
            HttpServletResponse response) {

        log.info("/reissue 진입");

        String authorizationHeader = requestEntity.getHeaders().getFirst(HttpHeaders.AUTHORIZATION);

        if (authorizationHeader == null && refreshToken == null) {
            throw new UnAuthorizedException("인증 정보가 제공되지 않았습니다. 로그인이 필요합니다.");
        }

        if (authorizationHeader != null && refreshToken == null) {
            throw new UnAuthorizedException("토큰 갱신 요청에 refresh token이 누락되었습니다.");
        }

        if (authorizationHeader == null) {
            if (!JWTUtil.validateRefreshToken(refreshToken)) {
                JWTUtil.deleteRefreshToken(refreshToken);
                throw new UnAuthorizedException("유효하지 않은 refresh token입니다. 다시 로그인해주세요.");
            }
            return getResponseEntity(refreshToken, response);
        }

        if (!authorizationHeader.startsWith("Bearer ")) {
            throw new UnAuthorizedException("잘못된 Authorization 헤더 형식입니다.");
        }

        String accessToken = authorizationHeader.substring(7);

        if (!JWTUtil.isExpired(accessToken)) {
            JWTUtil.deleteRefreshToken(refreshToken);
            throw new UnAuthorizedException("만료되지 않은 access token입니다. 보안상의 이유로 재인증이 필요합니다.");
        }

        if (JWTUtil.isExpired(refreshToken)) {
            JWTUtil.deleteRefreshToken(refreshToken);
            throw new UnAuthorizedException("만료된 refresh token입니다. 다시 로그인해주세요.");
        }

        return getResponseEntity(refreshToken, response);
    }

    private ResponseEntity<?> getResponseEntity(String refreshToken, HttpServletResponse response) {
        String username = JWTUtil.getUsername(refreshToken);
        String email = JWTUtil.getEmail(refreshToken);

        String newAccessToken = JWTUtil.createJwt("access", username, email);
        String newRefreshToken = JWTUtil.createJwt("refresh", username, email);

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + newAccessToken);

        cookieUtil.addRefreshTokenCookie(response, "refresh_token", newRefreshToken);

        JWTUtil.deleteRefreshToken(refreshToken);
        JWTUtil.save(username, newRefreshToken);

        return ResponseEntity.ok()
                .headers(headers)
                .build();
    }
}
