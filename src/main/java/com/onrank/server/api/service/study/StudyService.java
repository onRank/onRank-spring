package com.onrank.server.api.service.study;

import com.onrank.server.api.dto.file.FileMetadataDto;
import com.onrank.server.api.dto.study.AddStudyRequest;
import com.onrank.server.api.dto.study.AddStudyResponse;
import com.onrank.server.api.dto.study.StudyListResponse;
import com.onrank.server.api.service.cloud.S3Service;
import com.onrank.server.domain.file.FileCategory;
import com.onrank.server.domain.file.FileMetadata;
import com.onrank.server.domain.file.FileMetadataJpaRepository;
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
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class StudyService {

    private final StudentJpaRepository studentRepository;
    private final StudyJpaRepository studyRepository;
    private final MemberJpaRepository memberJpaRepository; // 이 줄 추가
    private final S3Service s3Service;
    private final FileMetadataJpaRepository fileMetadataRepository;


    public Optional<Study> findByStudyId(Long id) {
        return studyRepository.findByStudyId(id);
    }


    @Transactional
    public AddStudyResponse createStudy(AddStudyRequest addStudyRequest, String username) {

        Study study = studyRepository.save(addStudyRequest.toEntity());

        // url 추가
        String presignedUrl = s3Service.generatePresignedUrl(FileCategory.STUDY, study.getStudyId(), addStudyRequest.getFileName());

        // 현재 사용자 찾기
        Student student = studentRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Student not found"));

        // 사용자와 스터디 연결
        Member member = Member.builder()
                .student(student)
                .study(study)
                .memberRole(MemberRole.CREATOR)
                .memberJoiningAt(LocalDate.now())
                .build();
        memberJpaRepository.save(member);

        return AddStudyResponse.builder()
                .studyId(study.getStudyId())
                .fileName(addStudyRequest.getFileName())
                .uploadUrl(presignedUrl)
                .build();
    }

    public List<StudyListResponse> getStudyListResponsesByUsername(String username) {

        List<Study> studies = studyRepository.findAllByStudentUsername(username);

        return studies.stream()
                .map(study -> {
                    // 해당 스터디의 파일 중 하나 가져오기 (카테고리: STUDY)
                    List<FileMetadata> files = fileMetadataRepository
                            .findByCategoryAndEntityId(FileCategory.STUDY, study.getStudyId());

                    FileMetadataDto fileDto = null;
                    if (!files.isEmpty()) {
                        FileMetadata file = files.get(0); // 첫 번째 파일만 대표로 사용
                        fileDto = new FileMetadataDto(file, "onrank-bucket");
                    }

                    return new StudyListResponse(study, fileDto);
                })
                .toList();
    }
}
