package com.onrank.server.service.student;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface StudentJpaRepository extends JpaRepository<Student, Long> {

    // 이메일로 Student 검색
    Optional<Student> findByEmail(String email);

    // 이메일 존재 여부 확인
    boolean existsByEmail(String email);
}
