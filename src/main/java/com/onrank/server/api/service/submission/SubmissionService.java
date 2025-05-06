package com.onrank.server.api.service.submission;

import com.onrank.server.api.dto.common.ContextResponse;
import com.onrank.server.api.dto.common.MemberStudyContext;
import com.onrank.server.api.dto.file.FileMetadataDto;
import com.onrank.server.api.dto.submission.ScoreSubmissionRequest;
import com.onrank.server.api.dto.submission.SubmissionDetailResponse;
import com.onrank.server.api.dto.submission.SubmissionListResponse;
import com.onrank.server.api.service.file.FileService;
import com.onrank.server.api.service.member.MemberService;
import com.onrank.server.common.exception.CustomException;
import com.onrank.server.domain.assignment.Assignment;
import com.onrank.server.domain.assignment.AssignmentJpaRepository;
import com.onrank.server.domain.file.FileCategory;
import com.onrank.server.domain.member.Member;
import com.onrank.server.domain.submission.Submission;
import com.onrank.server.domain.submission.SubmissionJpaRepository;
import com.onrank.server.domain.submission.SubmissionStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.NoSuchElementException;

import static com.onrank.server.common.exception.CustomErrorInfo.ACCESS_DENIED;

@Service
@RequiredArgsConstructor
public class SubmissionService {

    private final SubmissionJpaRepository submissionRepository;
    private final AssignmentJpaRepository assignmentRepository;
    private final MemberService memberService;
    private final FileService fileService;

    /**
     * Assignment, Member로 Submission 조회
     */
    public Submission findByAssignmentAndMember(Assignment assignment, Member member) {
        return submissionRepository.findByAssignmentAndMember(assignment, member)
                .orElseThrow(() -> new NoSuchElementException("Submission not found"));
    }

    /**
     * 수정된 제출물 저장
     */
    @Transactional
    public void save(Submission submission) {
        submissionRepository.save(submission);
    }


    /**
     * 제출물 목록 조회 (관리자 기준)
     */
    @Transactional
    public ContextResponse<List<SubmissionListResponse>> getSubmissions(
            String username,
            Long studyId,
            Long assignmentId) throws IllegalAccessException {

        // CREATOR, HOST 만 가능
        if (!memberService.isMemberCreatorOrHost(username, studyId)) {
            throw new CustomException(ACCESS_DENIED);
        }
        // 컨텍스트 조회
        MemberStudyContext context = memberService.getContext(username, studyId);

        // 과제 조회
        Assignment assignment = assignmentRepository.findById(assignmentId)
                .orElseThrow(() -> new NoSuchElementException("Assignment not found"));

        // 제출물 조회
        List<Submission> submissions = submissionRepository.findAllByAssignment(assignment);

        // 변환
        List<SubmissionListResponse> responses = submissions.stream()
                .map(SubmissionListResponse::from)
                .toList();

        return new ContextResponse<>(context, responses);
    }

    /**
     * 제출물 상세 조회 (관리자 기준)
     */
    public ContextResponse<SubmissionDetailResponse> getSubmissionDetail(
            String username,
            Long studyId,
            Long assignmentId,
            Long submissionId) throws IllegalAccessException {

        // CREATOR, HOST 만 가능
        if (!memberService.isMemberCreatorOrHost(username, studyId)) {
            throw new CustomException(ACCESS_DENIED);
        }

        // 컨텍스트 조회
        MemberStudyContext context = memberService.getContext(username, studyId);

        // 과제 조회
        Assignment assignment = assignmentRepository.findById(assignmentId)
                .orElseThrow(() -> new NoSuchElementException("Assignment not found"));

        // 멤버 조회
        Member member = memberService.findMemberByUsernameAndStudyId(username, studyId)
                .orElseThrow(() -> new NoSuchElementException("Member not found"));

        // 제출물 조회 (과제 생성 시에 멤버별 제출물 엔티티를 생성해 놓음)
        Submission submission = this.findByAssignmentAndMember(assignment, member);

        // Url validation check
        if (!submission.getSubmissionId().equals(submissionId)) {
            throw new IllegalArgumentException("잘못된 URL 접근");
        }

        // 제출물 파일 조회 (있으면)
        List<FileMetadataDto> submissionFiles = List.of();
        if (submission.getSubmissionStatus() != SubmissionStatus.NOTSUBMITTED) {
            submissionFiles = fileService.getMultipleFileMetadata(FileCategory.SUBMISSION, submission.getSubmissionId());
        }

        SubmissionDetailResponse response = SubmissionDetailResponse.builder()
                .assignmentTitle(assignment.getAssignmentTitle())
                .assignmentDueDate(assignment.getAssignmentDueDate())
                .assignmentContent(assignment.getAssignmentContent())
                .memberId(member.getMemberId())
                .memberName(member.getStudent().getStudentName())
                .memberEmail(member.getStudent().getStudentEmail())
                .submissionCreatedAt(submission.getSubmissionCreatedAt())
                .submissionContent(submission.getSubmissionContent())
                .submissionFiles(submissionFiles)
                .submissionScore(submission.getSubmissionScore())  // null if not SCORED
                .submissionComment(submission.getSubmissionComment())  // null if not SCORED
                .build();

        return new ContextResponse<>(context, response);
    }

    /**
     * 제출물 채점 (관리자 기준 / 채점 페이지 / SUBMITTED, SCORED)
     */
    @Transactional
    public ContextResponse<Void> scoreSubmission(
            String username,
            Long studyId,
            Long assignmentId,
            Long submissionId,
            ScoreSubmissionRequest request) throws IllegalAccessException {

        // CREATOR, HOST 만 가능
        if (!memberService.isMemberCreatorOrHost(username, studyId)) {
            throw new CustomException(ACCESS_DENIED);
        }

        // 과제 조회
        Assignment assignment = assignmentRepository.findById(assignmentId)
                .orElseThrow(() -> new NoSuchElementException("Assignment not found"));

        // 멤버 조회
        Member member = memberService.findMemberByUsernameAndStudyId(username, studyId)
                .orElseThrow(() -> new NoSuchElementException("Member not found"));

        // 제출물 조회 (과제 생성 시에 멤버별 제출물 엔티티를 생성해 놓음)
        Submission submission = this.findByAssignmentAndMember(assignment, member);

        // Url validation check
        if (!submission.getSubmissionId().equals(submissionId)) {
            throw new IllegalArgumentException("잘못된 URL 접근");
        }

        // 제출물 상태가 SCORED일 경우 멤버 엔티티 과제 점수 속성 초기화
        if (submission.getSubmissionStatus() == SubmissionStatus.SCORED) {
            member.changeSubmissionPoint(member.getMemberSubmissionPoint() - submission.getSubmissionScore());
        }

        // 제출물 점수, 코멘트 업데이트
        submission.updateScore(request.getSubmissionScore(), request.getSubmissionComment());
        submissionRepository.save(submission);

        // 멤버 엔티티 과제 점수 속성 업데이트
        member.changeSubmissionPoint(member.getMemberSubmissionPoint() + request.getSubmissionScore());

        MemberStudyContext context = memberService.getContext(username, studyId);

        return new ContextResponse<>(context, null);
    }
}