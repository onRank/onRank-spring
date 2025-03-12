package com.onrank.server.api.service.study;

import com.onrank.server.api.dto.student.CreateStudyRequestDto;
import com.onrank.server.api.dto.study.MainpageStudyResponseDto;
import com.onrank.server.domain.member.Member;
import com.onrank.server.domain.student.Student;
import com.onrank.server.domain.student.StudentJpaRepository;
import com.onrank.server.domain.study.Study;
import com.onrank.server.domain.study.StudyJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class StudyService {

    private final StudentJpaRepository studentRepository;
    private final StudyJpaRepository studyRepository;

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
                    study.getStudyName(),
                    study.getStudyName(),
                    study.getStudyImageUrl());
            studies.add(studyDto);
        }

        return studies;
    }

    public Study createStudy(CreateStudyRequestDto requestDto) {
        Study study = Study.builder()
                .studyName(requestDto.getStudyName())
                .studyContent(requestDto.getStudyContent())
                .build();
        return studyRepository.save(study);
    }
}
