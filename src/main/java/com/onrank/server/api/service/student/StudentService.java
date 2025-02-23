package com.onrank.server.api.service.student;

import com.onrank.server.domain.student.Role;
import com.onrank.server.domain.student.Student;
import com.onrank.server.domain.student.StudentJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.Set;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class StudentService {

    private final StudentJpaRepository studentRepository;

    public Optional<Student> findByStudentId(Long studentId) {
        return studentRepository.findByStudentId(studentId);
    }

    public Optional<Student> findByUsername(String username) {
        return studentRepository.findByUsername(username);
    }

    public boolean checkIfNewUser(String username) {
        return !studentRepository.existsByUsername(username);
    }

    @Transactional
    public void createStudent(Student student) {
        studentRepository.save(student);
    }
}
