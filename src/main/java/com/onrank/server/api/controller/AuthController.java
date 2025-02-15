package com.onrank.server.api.controller;

import com.onrank.server.api.service.auth.CustomOAuth2User;
import com.onrank.server.api.service.student.StudentService;
import com.onrank.server.common.security.jwt.JwtTokenProvider;
import com.onrank.server.domain.student.RegisterStudentDto;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

@Slf4j
@Controller
@RequiredArgsConstructor
public class AuthController {

    private final StudentService studentService;
    private final JwtTokenProvider jwtTokenProvider;

    @GetMapping("/register-student")
    public String registerStudentForm(@CookieValue(value = "Authorization", required = false) String token, Model model) {
        if (token == null || !jwtTokenProvider.validateToken(token)) {
            log.warn("⚠️ 유효한 JWT가 없음 - 로그인 필요");
            return "redirect:/login"; // 인증되지 않은 경우 로그인 페이지로 이동
        }

        String email = jwtTokenProvider.getEmailFromToken(token);
        model.addAttribute("email", email);
        return "register-student";
    }

    @PostMapping("/register-student")
    public void registerStudent(@Valid @RequestBody RegisterStudentDto request, HttpServletResponse response) throws IOException {
        // 현재 로그인한 사용자 정보 가져오기
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !(authentication.getPrincipal() instanceof CustomOAuth2User)) {
            log.warn("⚠️ 인증되지 않은 사용자 - 로그인 필요");
            response.sendRedirect("/login"); // 인증되지 않은 경우 로그인 페이지로 이동
            return;
        }

        CustomOAuth2User oAuthUser = (CustomOAuth2User) authentication.getPrincipal();
        String email = oAuthUser.getEmail();

        // DTO -> 엔티티 변환 후 저장
        studentService.createMember(request.toEntity(email));

        // 회원가입 완료 후 JWT 재발급
        String token = jwtTokenProvider.generateToken(email);
        response.setHeader("Authorization", "Bearer " + token);

        // 스터디 페이지로 리디렉션
        response.sendRedirect("/studies");
    }
}

