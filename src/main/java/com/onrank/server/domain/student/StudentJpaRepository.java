package com.onrank.server.domain.student;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface StudentJpaRepository extends JpaRepository<Student, Long> {

    Optional<Student> findByUsername(String username);
}
