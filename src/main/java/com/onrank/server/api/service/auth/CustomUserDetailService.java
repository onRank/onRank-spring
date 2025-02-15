package com.onrank.server.api.service.auth;

import com.onrank.server.api.service.student.StudentService;
import com.onrank.server.common.security.jwt.CustomUserDetails;
import com.onrank.server.domain.student.Student;
import com.onrank.server.domain.student.StudentRepository;
import lombok.RequiredArgsConstructor;
import org.hibernate.validator.internal.constraintvalidators.hv.EmailValidator;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CustomUserDetailService implements UserDetailsService {

    private final StudentService studentService;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        return studentService.findByEmail(email)
                .map(CustomUserDetails::new) // 존재하면 CustomUserDetails 반환
                .orElse(null);
    }
}
