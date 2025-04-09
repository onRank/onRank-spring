package com.onrank.server.api.service.study;

import com.onrank.server.api.dto.file.FileMetadataDto;
import com.onrank.server.api.dto.member.MemberRoleResponse;
import com.onrank.server.api.dto.study.*;
import com.onrank.server.api.service.cloud.S3Service;
import com.onrank.server.api.service.member.MemberService;
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
    private final MemberJpaRepository memberJpaRepository;
    private final MemberService memberService;
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

    public StudyContext<StudyDetailResponse> getStudyDetail(Long studyId, String username) {
        Study study = studyRepository.findById(studyId)
                .orElseThrow(() -> new IllegalArgumentException("해당 스터디가 존재하지 않습니다."));

        List<FileMetadata> files = fileMetadataRepository
                .findByCategoryAndEntityId(FileCategory.STUDY, study.getStudyId());

        FileMetadataDto fileDto = null;
        if (!files.isEmpty()) {
            FileMetadata file = files.get(0); // 첫 번째 파일만 대표로 사용
            fileDto = new FileMetadataDto(file, "onrank-bucket");
        }

        MemberRoleResponse memberContext = memberService.getMyRoleInStudy(username, studyId);
        StudyDetailResponse detail = new StudyDetailResponse(study);

        return new StudyContext<>(memberContext, detail);
    }

    public StudyContext<AddStudyResponse> updateStudy(Long studyId, String username, StudyUpdateRequest request) {
        Study study = studyRepository.findById(studyId)
                .orElseThrow(() -> new IllegalArgumentException("Study not found"));

        study.update(request.getStudyName(), request.getStudyContent(), request.getPresentPoint(), request.getAbsentPoint(), request.getLatePoint(), request.getStudyStatus());


        // 기존 파일과 메타데이터 모두 삭제
        List<FileMetadata> existingFiles = s3Service.findFile(FileCategory.STUDY, studyId);
        existingFiles.forEach(file -> s3Service.deleteFile(file.getFilePath()));
        s3Service.deleteFileMetadata(FileCategory.STUDY, studyId);

        String presignedUrl = s3Service.generatePresignedUrl(FileCategory.STUDY, studyId, request.getFileName());

        AddStudyResponse response = AddStudyResponse.builder()
                .studyId(studyId)
                .fileName(request.getFileName())
                .uploadUrl(presignedUrl)
                .build();

        // 멤버 권한 포함
        MemberRoleResponse memberContext = memberService.getMyRoleInStudy(username, studyId);
        return new StudyContext<>(memberContext, response);
    }
}
