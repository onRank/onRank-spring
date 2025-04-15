package com.onrank.server.api.service.assignment;

import com.onrank.server.api.dto.assignment.AddAssignmentRequest;
import com.onrank.server.api.dto.assignment.AssignmentDetailResponse;
import com.onrank.server.api.dto.assignment.AssignmentSummaryResponse;
import com.onrank.server.api.dto.common.ContextResponse;
import com.onrank.server.api.dto.common.MemberStudyContext;
import com.onrank.server.api.dto.file.FileMetadataDto;
import com.onrank.server.api.dto.file.PresignedUrlResponse;
import com.onrank.server.api.service.file.FileService;
import com.onrank.server.api.service.member.MemberService;
import com.onrank.server.api.service.submission.SubmissionService;
import com.onrank.server.domain.assignment.Assignment;
import com.onrank.server.domain.assignment.AssignmentJpaRepository;
import com.onrank.server.domain.file.FileCategory;
import com.onrank.server.domain.member.Member;
import com.onrank.server.domain.member.MemberJpaRepository;
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

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AssignmentService {

    private final AssignmentJpaRepository assignmentRepository;
    private final StudyJpaRepository studyRepository;
    private final MemberJpaRepository memberRepository;
    private final SubmissionJpaRepository submissionRepository;
    private final FileService fileService;
    private final MemberService memberService;
    private final SubmissionService submissionService;

    /**
     * 과제 업로드 (스터디 멤버 전체에게 Submission 생성 포함)
     */
    @Transactional
    public ContextResponse<List<PresignedUrlResponse>> createAssignment(String username, Long studyId, AddAssignmentRequest request) {
        Study study = studyRepository.findById(studyId)
                .orElseThrow(() -> new IllegalArgumentException("Study not found"));

        Assignment assignment = request.toEntity(study);
        assignmentRepository.save(assignment);

        // Submission 자동 생성
        List<Member> members = memberRepository.findByStudy(study);
        for (Member member : members) {
            Submission submission = Submission.builder()
                    .assignment(assignment)
                    .member(member)
                    .submissionContent("") // 기본값
                    .submissionStatus(SubmissionStatus.NOTSUBMITTED)
                    .submissionCreatedAt(LocalDateTime.now())
                    .submissionComment(null) // 기본값
                    .submissionScore(null) // 기본값
                    .build();
            submissionRepository.save(submission);
            assignment.getSubmissions().add(submission);
        }

        // S3 presigned URL 발급 및 메타데이터 저장
        List<PresignedUrlResponse> responses = fileService.createMultiplePresignedUrls(FileCategory.ASSIGNMENT, assignment.getAssignmentId(), request.getFileNames());

        MemberStudyContext context = memberService.getContext(username, studyId);

        return new ContextResponse<>(context, responses);
    }

    /**
     * 과제 리스트 조회 (멤버 기준)
     */
    public ContextResponse<List<AssignmentSummaryResponse>> getAssignments(String username, Long studyId) {
        List<Assignment> assignments = assignmentRepository.findByStudyStudyId(studyId);

        Member member = memberService.findByUsernameAndStudyId(username, studyId)
                .orElseThrow(() -> new IllegalArgumentException("Member not found"));

        MemberStudyContext context = memberService.getContext(username, studyId);

        List<AssignmentSummaryResponse> responses = assignments.stream()
                .map(assignment -> AssignmentSummaryResponse.from(assignment, submissionService.findByAssignmentAndMember(assignment, member)))
                .toList();

        return new ContextResponse<>(context, responses);
    }

    /**
     * 과제 상세 조회 (멤버 기준)
     */
    public ContextResponse<AssignmentDetailResponse> getAssignmentDetail(String username, Long studyId, Long assignmentId) {
        // 멤버 조회
        Member member = memberService.findByUsernameAndStudyId(username, studyId)
                .orElseThrow(() -> new IllegalArgumentException("Member not found"));

        // 과제 조회
        Assignment assignment = assignmentRepository.findById(assignmentId)
                .orElseThrow(() -> new IllegalArgumentException("Assignment not found"));

        // 과제 파일 조회
        List<FileMetadataDto> assignmentFiles = fileService.getMultipleFileMetadata(FileCategory.ASSIGNMENT, assignmentId);

        // 멤버 제출물 조회
        Submission submission = submissionService.findByAssignmentAndMember(assignment, member);

        // 제출물 파일 조회 (있으면)
        List<FileMetadataDto> submissionFiles = List.of();
        if (submission.getSubmissionStatus() != SubmissionStatus.NOTSUBMITTED) {
            submissionFiles = fileService.getMultipleFileMetadata(FileCategory.SUBMISSION, submission.getSubmissionId());
        }

        AssignmentDetailResponse detailResponse = AssignmentDetailResponse.builder()
                .assignmentId(assignment.getAssignmentId())
                .assignmentTitle(assignment.getAssignmentTitle())
                .assignmentContent(assignment.getAssignmentContent())
                .assignmentDueDate(assignment.getAssignmentDueDate())
                .assignmentMaxPoint(assignment.getAssignmentMaxPoint())
                .submissionStatus(submission.getSubmissionStatus())
                .assignmentFiles(assignmentFiles)
                .submissionContent(submission.getSubmissionContent())
                .submissionFiles(submissionFiles)
                .submissionScore(submission.getSubmissionScore())
                .submissionComment(submission.getSubmissionComment())
                .build();

        MemberStudyContext context = memberService.getContext(username, studyId);

        return new ContextResponse<>(context, detailResponse);
    }
}
