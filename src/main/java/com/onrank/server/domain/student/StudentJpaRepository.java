package com.onrank.server.domain.student;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface StudentJpaRepository extends JpaRepository<Student, Long> {

    // 이메일로 Student 검색
    Optional<Student> findByEmail(String email);

    Optional<Student> findByUsername(String username);

    boolean existsByUsername(String username);
}
