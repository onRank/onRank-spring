package com.onrank.server.api.service.student;

import com.onrank.server.api.dto.student.AddStudentRequest;
import com.onrank.server.api.dto.student.StudentResponse;
import com.onrank.server.api.dto.study.StudyListResponse;
import com.onrank.server.api.service.study.StudyService;
import com.onrank.server.common.exception.CustomException;
import com.onrank.server.domain.student.Student;
import com.onrank.server.domain.student.StudentJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

import static com.onrank.server.common.exception.CustomErrorInfo.ACCESS_DENIED;
import static com.onrank.server.common.exception.CustomErrorInfo.STUDENT_NOT_FOUND;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class StudentService {

    private final StudentJpaRepository studentRepository;
    private final StudyService studyService;

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

    @Transactional
    public void updateStudent(String username, Long studentId, AddStudentRequest addStudentRequest) {

        Student student = findByStudentId(studentId)
                .orElseThrow(() -> new CustomException(STUDENT_NOT_FOUND));

        // 본인만 조회 가능
        if (!student.getUsername().equals(username)) {
            throw new CustomException(ACCESS_DENIED);
        }

        student.update(
                addStudentRequest.getStudentName(),
                addStudentRequest.getStudentSchool(),
                addStudentRequest.getStudentDepartment(),
                addStudentRequest.getStudentPhoneNumber()
        );
    }

    // 마이페이지 조회
    public StudentResponse getMyPage(String username, Long studentId) {

        Student student = findByStudentId(studentId)
                .orElseThrow(() -> new CustomException(STUDENT_NOT_FOUND));

        // 본인만 조회 가능
        if (!student.getUsername().equals(username)) {
            throw new CustomException(ACCESS_DENIED);
        }

        List<StudyListResponse> studyList = studyService.getStudyListResponsesByUsername(username);
        return StudentResponse.from(student, studyList);
    }
}