package com.onrank.server.api.service.student;

import com.onrank.server.api.dto.student.CreateStudyRequestDto;
import com.onrank.server.domain.member.Member;
import com.onrank.server.domain.member.MemberJpaRepository;
import com.onrank.server.domain.member.MemberRole;
import com.onrank.server.domain.student.Role;
import com.onrank.server.domain.student.Student;
import com.onrank.server.domain.student.StudentJpaRepository;
import com.onrank.server.domain.study.Study;
import com.onrank.server.domain.study.StudyJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.Optional;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class StudentService {

    private final StudentJpaRepository studentRepository;
    private final StudyJpaRepository studyRepository;
    private final MemberJpaRepository memberJpaRepository;

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

    @Transactional
    public void createStudent(Student student) {
        studentRepository.save(student);
    }

    @Transactional
    public Study createStudy(CreateStudyRequestDto requestDto, String username) {
        // 스터디 생성 코드
        Study.StudyBuilder builder = Study.builder()
                .studyName(requestDto.getStudyName())
                .studyContent(requestDto.getStudyContent()); // content에서 studyContent로 변경

        // 이미지 URL이 존재할 경우에만 세팅
        if (requestDto.getStudyImageUrl() != null && !requestDto.getStudyImageUrl().isEmpty()) {
            builder.studyImageUrl(requestDto.getStudyImageUrl()); // image에서 studyImageUrl로 변경
        }

        // 구글폼 URL이 존재할 경우에만 세팅
        if (requestDto.getStudyGoogleFormUrl() != null && !requestDto.getStudyGoogleFormUrl().isEmpty()) {
            builder.studyGoogleFormUrl(requestDto.getStudyGoogleFormUrl()); // googleForm에서 studyGoogleFormUrl로 변경
        }

        Study study = builder.build();
        study = studyRepository.save(study);

        // 현재 사용자 찾기
        Student student = studentRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Student not found"));

        // 사용자와 스터디 연결
        Member member = Member.builder()
                .student(student)
                .study(study)
                .memberRole(MemberRole.HOST)
                .memberJoiningAt(LocalDate.now())
                .build();

        memberJpaRepository.save(member);

        return study;
    }
}