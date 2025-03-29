package com.onrank.server.api.service.study;

import com.onrank.server.api.dto.student.AddStudyRequest;
import com.onrank.server.api.dto.student.CreateStudyRequestDto;
import com.onrank.server.api.dto.study.MainpageStudyResponseDto;
import com.onrank.server.api.service.cloud.S3Service;
import com.onrank.server.domain.file.FileCategory;
import com.onrank.server.domain.member.Member;
import com.onrank.server.domain.member.MemberJpaRepository;
import com.onrank.server.domain.member.MemberRole;  // 이 줄 추가
import com.onrank.server.domain.student.Student;
import com.onrank.server.domain.student.StudentJpaRepository;
import com.onrank.server.domain.study.Study;
import com.onrank.server.domain.study.StudyJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;  // 이 줄도 필요합니다
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class StudyService {

    private final StudentJpaRepository studentRepository;
    private final StudyJpaRepository studyRepository;
    private final MemberJpaRepository memberJpaRepository; // 이 줄 추가
    private final S3Service s3Service;


    public Optional<Study> findByStudyId(Long id) {
        return studyRepository.findByStudyId(id);
    }

    public List<MainpageStudyResponseDto> getStudiesByUsername(String username) {

        Student student = studentRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Student not found"));

        List<Member> members = student.getMembers();
        List<MainpageStudyResponseDto> studies = new ArrayList<>();

        for (Member member : members) {
            Study study = member.getStudy();
            MainpageStudyResponseDto studyDto = new MainpageStudyResponseDto(
                    study.getStudyId(),
                    study.getStudyName(),
                    study.getStudyContent(),
                    study.getStudyImageUrl(),
                    study.getStudyGoogleFormUrl());
            studies.add(studyDto);
        }

        return studies;
    }

//    // 수정된 메서드 (username 매개변수 추가)
//    @Transactional
//    public Study createStudy(CreateStudyRequestDto requestDto, String username) {
//        // 스터디 생성 코드...
//        Study.StudyBuilder builder = Study.builder()
//                .studyName(requestDto.getStudyName())
//                .studyContent(requestDto.getStudyContent());
//
//        // 이미지 URL이 존재할 경우에만 세팅
//        if (requestDto.getStudyImageUrl() != null && !requestDto.getStudyImageUrl().isEmpty()) {
//            builder.studyImageUrl(requestDto.getStudyImageUrl());
//        }
//
//        // 구글폼 URL이 존재할 경우에만 세팅
//        if (requestDto.getStudyGoogleFormUrl() != null && !requestDto.getStudyGoogleFormUrl().isEmpty()) {
//            builder.studyGoogleFormUrl(requestDto.getStudyGoogleFormUrl());
//        }
//
//        Study study = builder.build();
//        study = studyRepository.save(study);
//
//        // 현재 사용자 찾기
//        Student student = studentRepository.findByUsername(username)
//                .orElseThrow(() -> new RuntimeException("Student not found"));
//
//        // 사용자와 스터디 연결
//        Member member = Member.builder()
//                .student(student)
//                .study(study)
//                .memberRole(MemberRole.HOST)
//                .memberJoiningAt(LocalDate.now())
//                .build();
//
//        memberJpaRepository.save(member);
//
//        return study;
//    }
    // 수정된 메서드 (username 매개변수 추가)
    @Transactional
    public Map<String, Object> createStudy(AddStudyRequest addStudyRequest, String username) {

        Study study = studyRepository.save(addStudyRequest.toEntity());

        // url 추가
        String presignedUrl = s3Service.generatePresignedUrl(FileCategory.STUDY, study.getStudyId(), addStudyRequest.getFileName());

//        // 구글폼 URL이 존재할 경우에만 세팅
//        if (requestDto.getStudyGoogleFormUrl() != null && !requestDto.getStudyGoogleFormUrl().isEmpty()) {
//            builder.studyGoogleFormUrl(requestDto.getStudyGoogleFormUrl());
//        }

//        Study study = builder.build();
//        study = studyRepository.save(study);

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
        return Map.of(
                "studyId", study.getStudyId(),
                "fileName", addStudyRequest.getFileName(),
                "uploadUrl", presignedUrl
        );
    }
}
