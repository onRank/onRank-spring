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
        // 스터디 생성
        Study.StudyBuilder builder = Study.builder()
                .studyName(requestDto.getStudyName())
                .studyContent(requestDto.getContent());

        // 이미지 URL이 존재할 경우에만 세팅
        if (requestDto.getImage() != null && !requestDto.getImage().isEmpty()) {
            builder.studyImageUrl(requestDto.getImage());
        }

        // 구글폼 URL이 존재할 경우에만 세팅
        if (requestDto.getGoogleForm() != null && !requestDto.getGoogleForm().isEmpty()) {
            builder.studyGoogleFormUrl(requestDto.getGoogleForm());
        }

        Study study = builder.build();
        study = studyRepository.save(study);

        // 현재 사용자 찾기
        Student student = studentRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Student not found"));

        // 사용자와 스터디 연결 - 역할을 HOST로 설정
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