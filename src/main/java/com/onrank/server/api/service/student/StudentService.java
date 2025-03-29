package com.onrank.server.api.service.student;

import com.onrank.server.domain.student.Student;
import com.onrank.server.domain.student.StudentJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class StudentService {

    private final StudentJpaRepository studentRepository;

    public String findStudentNameByUsername (String username) {
        Student student = studentRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("Username " + username + " not found"));

        return student.getStudentName();
    }

    public Optional<Student> findByStudentId(Long studentId) {
        return studentRepository.findByStudentId(studentId);
    }

    public Optional<Student> findByStudentEmail(String studentEmail) {
        return studentRepository.findByStudentEmail(studentEmail);
    }

    public Optional<Student> findByUsername(String username) {
        return studentRepository.findByUsername(username);
    }

    public boolean checkIfNewUser(String username) {
        return !studentRepository.existsByUsername(username);
    }

    public boolean checkIfExist(String studentEmil) {
        return studentRepository.existsByStudentEmail(studentEmil);
    }

    @Transactional
    public void createStudent(Student student) {
        studentRepository.save(student);
    }
}