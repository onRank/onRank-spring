package com.onrank.server.api.service.student;

import com.onrank.server.domain.student.Student;
import com.onrank.server.domain.student.StudentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class StudentService {

    private final StudentRepository studentRepository;

    public Optional<Student> findById(Long id) {
        return studentRepository.findById(id);
    }

    public Optional<Student> findByEmail(String email) {
        return studentRepository.findByEmail(email);
    }

    /**
     * 새로운 Student 등록
     */
    @Transactional
    public void createMember(Student student) {
        studentRepository.save(student);
    }
}
