package com.onrank.server.common.security.jwt;

import com.onrank.server.domain.student.Student;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

@Getter
public class CustomUserDetails implements UserDetails { // 인증된 사용자 객체

    private final String email;

    public CustomUserDetails(Student student) {
        this.email = student.getEmail(); // Google 로그인에서 가져온 이메일
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority("user"));
    }

    @Override
    public String getPassword() {
        return null; // OAuth2 기반 인증이므로 패스워드는 null
    }

    @Override
    public String getUsername() {
        return email; // Spring Security에서 사용자를 식별할 값 (이메일 사용)
    }
}
