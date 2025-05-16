package com.onrank.server.api.service.assignment;

import com.onrank.server.api.dto.assignment.*;
import com.onrank.server.api.dto.common.ContextResponse;
import com.onrank.server.api.dto.common.MemberStudyContext;
import com.onrank.server.api.dto.file.FileMetadataDto;
import com.onrank.server.api.dto.file.PresignedUrlResponse;
import com.onrank.server.api.dto.submission.UpdateSubmissionRequest;
import com.onrank.server.api.service.file.FileService;
import com.onrank.server.api.service.member.MemberService;
import com.onrank.server.api.service.notification.NotificationService;
import com.onrank.server.api.service.submission.SubmissionService;
import com.onrank.server.common.exception.CustomException;
import com.onrank.server.domain.assignment.Assignment;
import com.onrank.server.domain.assignment.AssignmentJpaRepository;
import com.onrank.server.domain.file.FileCategory;
import com.onrank.server.domain.member.Member;
import com.onrank.server.domain.member.MemberJpaRepository;
import com.onrank.server.domain.notification.NotificationCategory;
import com.onrank.server.domain.study.Study;
import com.onrank.server.domain.study.StudyJpaRepository;
import com.onrank.server.domain.submission.Submission;
import com.onrank.server.domain.submission.SubmissionJpaRepository;
import com.onrank.server.domain.submission.SubmissionStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.NoSuchElementException;

import static com.onrank.server.common.exception.CustomErrorInfo.ACCESS_DENIED;
import static com.onrank.server.common.exception.CustomErrorInfo.NOT_STUDY_MEMBER;

@Slf4j
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
    private final NotificationService notificationService;

    public Assignment findById(Long assignmentId) {
        return assignmentRepository.findById(assignmentId)
                .orElseThrow(() -> new NoSuchElementException("Assignment not found"));
    }

    /**
     * 과제 업로드 (관리자 기준 / 스터디 멤버 전체에게 Submission 생성 포함)
     */
    @Transactional
    public ContextResponse<List<PresignedUrlResponse>> createAssignment(String username, Long studyId, CreateAssignmentRequest request) {

        // CREATOR, HOST 만 가능
        if (!memberService.isMemberCreatorOrHost(username, studyId)) {
            throw new CustomException(ACCESS_DENIED);
        }

        // 컨텍스트 조회
        MemberStudyContext context = memberService.getContext(username, studyId);

        // 스터디 조회
        Study study = studyRepository.findById(studyId)
                .orElseThrow(() -> new NoSuchElementException("Study not found"));

        // Assignment 생성 및 저장
        Assignment assignment = request.toEntity(study);
        assignmentRepository.save(assignment);

        // Submission 생성 및 저장
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

        // 알림 생성
        notificationService.createNotification(NotificationCategory.ASSIGNMENT, assignment.getAssignmentId(), studyId, assignment.getAssignmentTitle(), assignment.getAssignmentContent(),
                "/studies/" + studyId + "/assignments/" + assignment.getAssignmentId());

        return new ContextResponse<>(context, responses);
    }

    /**
     * 과제 수정 페이지 (관리자 기준)
     */
    public ContextResponse<AssignmentEditResponse> getAssignmentForEdit(String username, Long studyId, Long assignmentId) throws IllegalAccessException {

        // CREATOR, HOST 만 가능
        if (!memberService.isMemberCreatorOrHost(username, studyId)) {
            throw new CustomException(ACCESS_DENIED);
        }

        // 컨텍스트 조회
        MemberStudyContext context = memberService.getContext(username, studyId);

        // 과제 조회
        Assignment assignment = this.findById(assignmentId);

        // 파일 조회
        List<FileMetadataDto> assignmentFiles = fileService.getMultipleFileMetadata(FileCategory.ASSIGNMENT, assignmentId);

        AssignmentEditResponse response = AssignmentEditResponse.builder()
                .assignmentId(assignment.getAssignmentId())
                .assignmentTitle(assignment.getAssignmentTitle())
                .assignmentContent(assignment.getAssignmentContent())
                .assignmentDueDate(assignment.getAssignmentDueDate())
                .assignmentMaxPoint(assignment.getAssignmentMaxPoint())
                .assignmentFiles(assignmentFiles)
                .build();

        return new ContextResponse<>(context, response);
    }


    /**
     * 과제 수정 (관리자 기준)
     */
    @Transactional
    public ContextResponse<List<PresignedUrlResponse>> updateAssignment(
            String username, Long studyId, Long assignmentId, UpdateAssignmentRequest request) throws IllegalAccessException {

        // CREATOR, HOST 만 가능
        if (!memberService.isMemberCreatorOrHost(username, studyId)) {
            throw new CustomException(ACCESS_DENIED);
        }

        // 컨텍스트
        MemberStudyContext context = memberService.getContext(username, studyId);

        // 과제 조회
        Assignment assignment = this.findById(assignmentId);

        // 과제 정보 업데이트
        assignment.update(
                request.getAssignmentTitle(),
                request.getAssignmentContent(),
                request.getAssignmentDueDate(),
                request.getAssignmentMaxPoint()
        );

        // 파일 처리
        List<PresignedUrlResponse> responses = fileService.replaceFiles(
                FileCategory.ASSIGNMENT,
                assignmentId,
                request.getRemainingFileIds(),
                request.getNewFileNames()
        );

        // 알림 생성
        notificationService.createNotification(NotificationCategory.ASSIGNMENT, assignment.getAssignmentId(), studyId, assignment.getAssignmentTitle(), assignment.getAssignmentContent(),
                "/studies/" + studyId + "/assignments/" + assignment.getAssignmentId());

        return new ContextResponse<>(context, responses);
    }

    /**
     * 과제 삭제 (관리자 기준)
     */
    @Transactional
    public ContextResponse<Void> deleteAssignment(String username, Long studyId, Long assignmentId) throws IllegalAccessException {

        // CREATOR, HOST 만 가능
        if (!memberService.isMemberCreatorOrHost(username, studyId)) {
            throw new CustomException(ACCESS_DENIED);
        }

        // 컨텍스트 조회
        MemberStudyContext context = memberService.getContext(username, studyId);

        // 과제 조회
        Assignment assignment = this.findById(assignmentId);

        // 알림 삭제
        notificationService.deleteNotificationByEntity(NotificationCategory.ASSIGNMENT, assignmentId);

        // 과제 파일 삭제
        fileService.deleteAllFilesAndMetadata(FileCategory.ASSIGNMENT, assignmentId);

        // 제출물 관련 파일 및 제출물 삭제
        List<Submission> submissions = assignment.getSubmissions();
        for (Submission submission : submissions) {
            fileService.deleteAllFilesAndMetadata(FileCategory.SUBMISSION, submission.getSubmissionId());
        }

        // 과제 삭제 (Cascade로 제출물도 삭제되도록 설정되어 있다고 가정)
        assignmentRepository.delete(assignment);

        return new ContextResponse<>(context, null);
    }

    /**
     * 과제 목록 조회 (멤버 기준)
     */
    public ContextResponse<List<AssignmentListResponse>> getAssignments(String username, Long studyId) {
        // 스터디 멤버만 가능
        if (!memberService.isMemberInStudy(username, studyId)) {
            throw new CustomException(NOT_STUDY_MEMBER);
        }

        List<Assignment> assignments = assignmentRepository.findByStudyStudyId(studyId);

        Member member = memberService.findMemberByUsernameAndStudyId(username, studyId)
                .orElseThrow(() -> new NoSuchElementException("Member not found"));

        MemberStudyContext context = memberService.getContext(username, studyId);

        log.info("assignments");

        List<AssignmentListResponse> responses = assignments.stream()
                .map(assignment -> AssignmentListResponse.from(assignment, submissionService.findByAssignmentIdAndMemberId(assignment.getAssignmentId(), member.getMemberId())))
                .toList();

        return new ContextResponse<>(context, responses);
    }

    /**
     * 과제 상세 조회 (멤버 기준)
     */
    public ContextResponse<AssignmentDetailResponse> getAssignmentDetail(String username, Long studyId, Long assignmentId) {

        // 스터디 멤버만 가능
        if (!memberService.isMemberInStudy(username, studyId)) {
            throw new CustomException(NOT_STUDY_MEMBER);
        }

        // 컨텍스트 조회
        MemberStudyContext context = memberService.getContext(username, studyId);

        // 과제 조회
        Assignment assignment = this.findById(assignmentId);

        // 과제 파일 조회
        List<FileMetadataDto> assignmentFiles = fileService.getMultipleFileMetadata(FileCategory.ASSIGNMENT, assignmentId);

        // 멤버 조회
        Member member = memberService.findMemberByUsernameAndStudyId(username, studyId)
                .orElseThrow(() -> new NoSuchElementException("Member not found"));

        // 멤버 제출물 조회
        Submission submission = submissionService.findByAssignmentIdAndMemberId(assignment.getAssignmentId(), member.getMemberId());

        // 제출물 파일 조회 (있으면)
        List<FileMetadataDto> submissionFiles = List.of();
        if (submission.getSubmissionStatus() != SubmissionStatus.NOTSUBMITTED) {
            submissionFiles = fileService.getMultipleFileMetadata(FileCategory.SUBMISSION, submission.getSubmissionId());
        }

        AssignmentDetailResponse detailResponse = AssignmentDetailResponse.builder()
                .assignmentId(assignment.getAssignmentId())
                .assignmentTitle(assignment.getAssignmentTitle())
                .submissionStatus(submission.getSubmissionStatus())
                .assignmentDueDate(assignment.getAssignmentDueDate())
                .assignmentMaxPoint(assignment.getAssignmentMaxPoint())
                .assignmentContent(assignment.getAssignmentContent())
                .assignmentFiles(assignmentFiles)
                .submissionContent(submission.getSubmissionContent())
                .submissionFiles(submissionFiles)
                .submissionScore(submission.getSubmissionScore())
                .submissionComment(submission.getSubmissionComment())
                .build();

        return new ContextResponse<>(context, detailResponse);
    }

    /**
     * 과제 제출 (멤버 기준)
     */
    @Transactional
    public ContextResponse<List<PresignedUrlResponse>> submitAssignment(
            String username,
            Long studyId,
            Long assignmentId,
            CreateSubmissionRequest request) {

        // 스터디 멤버만 가능
        if (!memberService.isMemberInStudy(username, studyId)) {
            throw new CustomException(NOT_STUDY_MEMBER);
        }

        // 컨텍스트 조회
        MemberStudyContext context = memberService.getContext(username, studyId);

        // 과제 & 멤버 조회
        Assignment assignment = this.findById(assignmentId);
        Member member = memberService.findMemberByUsernameAndStudyId(username, studyId)
                .orElseThrow(() -> new NoSuchElementException("Member not found"));

        // 제출물 조회 (과제 생성 시에 멤버별 제출물 엔티티를 생성해 놓음)
        Submission submission = submissionService.findByAssignmentIdAndMemberId(assignmentId, member.getMemberId());

        // 제출 내용 업데이트
        submission.updateSubmission(request.getSubmissionContent(), LocalDateTime.now());
        submissionService.save(submission);

        // S3 presigned URL 발급 및 메타데이터 저장
        List<PresignedUrlResponse> responses = fileService.createMultiplePresignedUrls(FileCategory.SUBMISSION, submission.getSubmissionId(), request.getFileNames());

        return new ContextResponse<>(context, responses);
    }

    /**
     * 과제 재제출 (멤버 기준)
     */
    @Transactional
    public ContextResponse<List<PresignedUrlResponse>> resubmitAssignment(
            String username,
            Long studyId,
            Long assignmentId,
            UpdateSubmissionRequest request) throws IllegalAccessException {

        // 스터디 멤버만 가능
        if (!memberService.isMemberInStudy(username, studyId)) {
            throw new CustomException(NOT_STUDY_MEMBER);
        }

        // 컨텍스트
        MemberStudyContext context = memberService.getContext(username, studyId);

        // 과제 & 멤버 조회
        Assignment assignment = this.findById(assignmentId);
        Member member = memberService.findMemberByUsernameAndStudyId(username, studyId)
                .orElseThrow(() -> new NoSuchElementException("Member not found"));

        // 제출물 조회
        Submission submission = submissionService.findByAssignmentIdAndMemberId(assignmentId, member.getMemberId());

        // 상태 체크: NOTSUBMITTED는 수정할 수 없음
        if (submission.getSubmissionStatus() == SubmissionStatus.NOTSUBMITTED) {
            throw new IllegalStateException("제출하지 않은 과제는 수정할 수 없습니다.");
        }

        // 제출물 상태가 SCORED일 경우 멤버 엔티티 과제 점수 속성 초기화
        if (submission.getSubmissionStatus() == SubmissionStatus.SCORED) {
            member.changeSubmissionPoint(member.getMemberSubmissionPoint() - submission.getSubmissionScore());
        }

        // 제출 내용 업데이트
        submission.updateSubmission(request.getSubmissionContent(), LocalDateTime.now());
        submissionService.save(submission);

        // 파일 처리 (기존 파일 유지 + 새 파일 업로드)
        List<PresignedUrlResponse> responses = fileService.replaceFiles(
                FileCategory.SUBMISSION,
                submission.getSubmissionId(),
                request.getRemainingFileIds(),
                request.getNewFileNames()
        );

        return new ContextResponse<>(context, responses);
    }

    @Transactional
    public void createSubmissionsToNewMember(Member member) {


    }
}
