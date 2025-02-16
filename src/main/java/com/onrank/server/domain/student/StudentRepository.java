package com.onrank.server.domain.student;

import java.util.Optional;

public interface StudentRepository {

    // Id 기반 조회
    Optional<Student> findById(Long id);

    // 이메일을 기반으로 Student 조회
    Optional<Student> findByEmail(String email);

    // Student 저장
    Student save(Student student);
}
