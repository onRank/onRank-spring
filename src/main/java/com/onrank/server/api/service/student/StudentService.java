package com.onrank.server.api.service.student;

import com.onrank.server.domain.student.Student;
import com.onrank.server.domain.student.StudentJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class StudentService {

    private final StudentJpaRepository studentRepository;

    public Optional<Student> findById(Long id) {
        return studentRepository.findById(id);
    }

    public Optional<Student> findByUsername(String username) {
        return studentRepository.findByEmail(username);
    }

    @Transactional
    public void createStudent(Student student) {
        studentRepository.save(student);
    }
}
