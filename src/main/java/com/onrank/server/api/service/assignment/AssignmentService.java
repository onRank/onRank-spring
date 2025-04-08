package com.onrank.server.api.service.assignment;

import com.onrank.server.api.dto.assignment.AddAssignmentRequest;
import com.onrank.server.api.dto.assignment.AssignmentResponse;
import com.onrank.server.api.dto.file.FileMetadataDto;
import com.onrank.server.api.service.cloud.S3Service;
import com.onrank.server.domain.assignment.Assignment;
import com.onrank.server.domain.assignment.AssignmentJpaRepository;
import com.onrank.server.domain.file.FileCategory;
import com.onrank.server.domain.file.FileMetadata;
import com.onrank.server.domain.member.Member;
import com.onrank.server.domain.member.MemberJpaRepository;
import com.onrank.server.domain.student.Student;
import com.onrank.server.domain.student.StudentJpaRepository;
import com.onrank.server.domain.study.Study;
import com.onrank.server.domain.study.StudyJpaRepository;
import com.onrank.server.domain.submission.Submission;
import com.onrank.server.domain.submission.SubmissionJpaRepository;
import com.onrank.server.domain.submission.SubmissionStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AssignmentService {

    private final AssignmentJpaRepository assignmentRepository;
    private final StudyJpaRepository studyRepository;
    private final StudentJpaRepository studentRepository;
    private final MemberJpaRepository memberRepository;
    private final SubmissionJpaRepository submissionRepository;
    private final S3Service s3Service;

    /**
     * 과제 리스트 조회 (현재 사용자 기준)
     */
    public List<AssignmentResponse> getAssignments(Long studyId) {
        return null;
    }

    /**
     * 과제 생성 (스터디 멤버 전체에게 Submission 생성 포함)
     */
    @Transactional
    public List<FileMetadataDto> createAssignment(Long studyId, AddAssignmentRequest request) {
        Study study = studyRepository.findById(studyId)
                .orElseThrow(() -> new IllegalArgumentException("Study not found"));

        Assignment assignment = request.toEntity(study);
        assignmentRepository.save(assignment);

        // Submission 자동 생성
        List<Member> members = memberRepository.findByStudyStudyId(studyId);
        for (Member member : members) {
            Submission submission = Submission.builder()
                    .assignment(assignment)
                    .member(member)
                    .submissionContent("") // 기본값
                    .submissionStatus(SubmissionStatus.NOTSUBMITTED)
                    .submissionCreatedAt(LocalDateTime.now())
                    .build();
            submissionRepository.save(submission);
        }

        // S3 presigned URL 발급 및 메타데이터 저장
        s3Service.uploadFilesWithMetadata(FileCategory.ASSIGNMENT, assignment.getAssignmentId(), request.getFileNames());

        List<FileMetadata> metadataList = s3Service.findFile(FileCategory.ASSIGNMENT, assignment.getAssignmentId());
        return metadataList.stream()
                .map(file -> new FileMetadataDto(file, s3Service.getBucketName()))
                .collect(Collectors.toList());
    }
}
