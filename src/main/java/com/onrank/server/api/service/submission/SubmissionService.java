package com.onrank.server.api.service.submission;

import com.onrank.server.api.dto.common.ContextResponse;
import com.onrank.server.api.dto.common.MemberStudyContext;
import com.onrank.server.api.dto.file.FileMetadataDto;
import com.onrank.server.api.dto.submission.ScoreSubmissionRequest;
import com.onrank.server.api.dto.submission.SubmissionDetailResponse;
import com.onrank.server.api.dto.submission.SubmissionListResponse;
import com.onrank.server.api.service.file.FileService;
import com.onrank.server.api.service.member.MemberService;
import com.onrank.server.api.service.notification.NotificationService;
import com.onrank.server.common.exception.CustomException;
import com.onrank.server.domain.assignment.Assignment;
import com.onrank.server.domain.assignment.AssignmentJpaRepository;
import com.onrank.server.domain.file.FileCategory;
import com.onrank.server.domain.member.Member;
import com.onrank.server.domain.notification.NotificationCategory;
import com.onrank.server.domain.submission.Submission;
import com.onrank.server.domain.submission.SubmissionJpaRepository;
import com.onrank.server.domain.submission.SubmissionStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static com.onrank.server.common.exception.CustomErrorInfo.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class SubmissionService {

    private final SubmissionJpaRepository submissionRepository;
    private final AssignmentJpaRepository assignmentRepository;
    private final MemberService memberService;
    private final FileService fileService;
    private final NotificationService notificationService;

    /**
     * Assignment, Member로 Submission 조회
     */
    public Submission findByAssignmentIdAndMemberId(Long assignmentId, Long memberId) {
        return submissionRepository.findByAssignmentAssignmentIdAndMemberMemberId(assignmentId, memberId)
                .orElseThrow(() -> new CustomException(SUBMISSION_NOT_FOUND));
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
    public ContextResponse<List<SubmissionListResponse>> getSubmissions(String username, Long studyId, Long assignmentId) {

        // CREATOR, HOST 만 가능
        if (!memberService.isMemberCreatorOrHost(username, studyId)) {
            throw new CustomException(ACCESS_DENIED);
        }

        // 과제 조회
        Assignment assignment = assignmentRepository.findById(assignmentId)
                .orElseThrow(() -> new CustomException(ASSIGNMENT_NOT_FOUND));

        // 제출물 조회
        List<Submission> submissions = submissionRepository.findAllByAssignment(assignment);

        // 변환
        List<SubmissionListResponse> responses = submissions.stream()
                .map(SubmissionListResponse::from)
                .toList();

        // 컨텍스트 조회
        MemberStudyContext context = memberService.getContext(username, studyId);

        return new ContextResponse<>(context, responses);
    }

    /**
     * 제출물 상세 조회 (관리자 기준)
     */
    public ContextResponse<SubmissionDetailResponse> getSubmissionDetail(
            String username,
            Long studyId,
            Long assignmentId,
            Long submissionId) {

        // CREATOR, HOST 만 가능
        if (!memberService.isMemberCreatorOrHost(username, studyId)) {
            throw new CustomException(ACCESS_DENIED);
        }

        // 컨텍스트 조회
        MemberStudyContext context = memberService.getContext(username, studyId);

        // 과제 조회
        Assignment assignment = assignmentRepository.findById(assignmentId)
                .orElseThrow(() -> new CustomException(ASSIGNMENT_NOT_FOUND));

        // 과제 파일 조회
        List<FileMetadataDto> assignmentFiles = fileService.getMultipleFileMetadata(FileCategory.ASSIGNMENT, assignmentId);

        // 제출물 조회
        Submission submission = submissionRepository.findById(submissionId)
                .orElseThrow(() -> new CustomException(SUBMISSION_NOT_FOUND));

        // 제출물 멤버 조회
        Member member = submission.getMember();

//        // Url validation check
//        if (!submission.getSubmissionId().equals(submissionId)) {
//            log.info("submission.getSubmissionId(): {}, submission: {}", submission.getSubmissionId(), submission);
//            throw new IllegalArgumentException("잘못된 URL 접근");
//        }

        // 제출물 파일 조회 (있으면)
        List<FileMetadataDto> submissionFiles = List.of();
        if (submission.getSubmissionStatus() != SubmissionStatus.NOTSUBMITTED) {
            submissionFiles = fileService.getMultipleFileMetadata(FileCategory.SUBMISSION, submission.getSubmissionId());
        }

        SubmissionDetailResponse response = SubmissionDetailResponse.builder()
                .assignmentTitle(assignment.getAssignmentTitle())
                .assignmentDueDate(assignment.getAssignmentDueDate())
                .assignmentContent(assignment.getAssignmentContent())
                .assignmentFiles(assignmentFiles)
                .memberId(member.getMemberId())
                .memberName(member.getStudent().getStudentName())
                .memberEmail(member.getStudent().getStudentEmail())
                .submissionCreatedAt(submission.getSubmissionCreatedAt())
                .submissionContent(submission.getSubmissionContent())
                .submissionFiles(submissionFiles)
                .assignmentMaxPoint(assignment.getAssignmentMaxPoint())
                .submissionScore(submission.getSubmissionScore())  // null if not SCORED
                .submissionComment(submission.getSubmissionComment())  // null if not SCORED
                .build();

        return new ContextResponse<>(context, response);
    }

    /**
     * 제출물 채점 (관리자 기준 / 채점 페이지 / SUBMITTED, SCORED)
     */
    @Transactional
    public ContextResponse<Void> scoreSubmission(String username, Long studyId
            , Long assignmentId, Long submissionId, ScoreSubmissionRequest request) {

        // CREATOR, HOST 만 가능
        if (!memberService.isMemberCreatorOrHost(username, studyId)) {
            throw new CustomException(ACCESS_DENIED);
        }

        Assignment assignment = assignmentRepository.findById(assignmentId)
                .orElseThrow(() -> new CustomException(ASSIGNMENT_NOT_FOUND));

        Submission submission = submissionRepository.findById(submissionId)
                .orElseThrow(() -> new CustomException(SUBMISSION_NOT_FOUND));

        // SubmissionStatus.NOTSUBMITTED 에 대해서는 채점 불가
        if(submission.getSubmissionStatus() == SubmissionStatus.NOTSUBMITTED) {
            throw new CustomException(SUBMISSION_NOT_SUBMITTED);
        }

        Member member = submission.getMember();

        // Url validation check
        Submission checkSubmission = findByAssignmentIdAndMemberId(assignmentId, member.getMemberId());
        if (!checkSubmission.getSubmissionId().equals(submissionId)) {
            throw new CustomException(INVALID_REQUEST);
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

        // 컨텍스트 조회
        MemberStudyContext context = memberService.getContext(username, studyId);

        // 알림 생성
        notificationService.createNotification(NotificationCategory.SUBMISSION, submissionId, studyId, assignment.getAssignmentTitle() + " 과제가 채점 되었습니다!"
                ,submission.getSubmissionScore() +  submission.getSubmissionComment(),
                "/studies/" + studyId + "/assignments/" + assignmentId);

        return new ContextResponse<>(context, null);
    }
}