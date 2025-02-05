package com.onrank.server.domain.student;


import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import javax.swing.*;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class StudentRepositoryImpl implements StudentRepository {

    private final StudentJpaRepository jpaRepository;

    @Override
    public Optional<Student> findById(Long id) {
        return jpaRepository.findById(id);
    }

    @Override
    public Optional<Student> findByEmail(String email) {
        return jpaRepository.findByEmail(email);
    }

    @Override
    public void save(Student student) {
        jpaRepository.save(student);
    }

    @Override
    public boolean existByEmail(String Email) {
        return jpaRepository.existsByEmail(Email);
    }
}
