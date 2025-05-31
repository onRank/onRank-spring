package com.onrank.server.api.service.member;

import com.onrank.server.api.dto.common.ContextResponse;
import com.onrank.server.api.dto.common.MemberStudyContext;
import com.onrank.server.api.dto.file.FileMetadataDto;
import com.onrank.server.api.dto.member.AddMemberRequest;
import com.onrank.server.api.dto.member.MemberListResponse;
import com.onrank.server.api.dto.member.MemberManagementResponse;
import com.onrank.server.api.service.attendance.AttendanceService;
import com.onrank.server.api.service.notification.NotificationService;
import com.onrank.server.common.exception.CustomException;
import com.onrank.server.domain.assignment.Assignment;
import com.onrank.server.domain.assignment.AssignmentJpaRepository;
import com.onrank.server.domain.file.FileCategory;
import com.onrank.server.domain.file.FileMetadata;
import com.onrank.server.domain.file.FileMetadataJpaRepository;
import com.onrank.server.domain.member.Member;
import com.onrank.server.domain.member.MemberJpaRepository;
import com.onrank.server.domain.member.MemberRole;
import com.onrank.server.domain.student.Student;
import com.onrank.server.domain.student.StudentJpaRepository;
import com.onrank.server.domain.study.Study;
import com.onrank.server.domain.study.StudyJpaRepository;
import com.onrank.server.domain.submission.Submission;
import com.onrank.server.domain.submission.SubmissionJpaRepository;
import com.onrank.server.domain.submission.SubmissionStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static com.onrank.server.common.exception.CustomErrorInfo.*;


@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MemberService {

    private final MemberJpaRepository memberRepository;
    private final StudyJpaRepository studyRepository;
    private final StudentJpaRepository studentRepository;
    private final FileMetadataJpaRepository fileMetadataRepository;
    private final AttendanceService attendanceService;
    private final AssignmentJpaRepository assignmentRepository;
    private final SubmissionJpaRepository submissionRepository;
    private final NotificationService notificationService;

    @Value("${cloud.aws.s3.bucket}")
    private String bucketName;

    public MemberStudyContext getContext(String username, Long studyId) {
        Member member = findMemberByUsernameAndStudyId(username, studyId)
                .orElseThrow(() -> new CustomException(MEMBER_NOT_FOUND));

        List<FileMetadata> files = fileMetadataRepository
                .findByCategoryAndEntityId(FileCategory.STUDY, member.getStudy().getStudyId());

        FileMetadataDto fileDto = null;
        if (!files.isEmpty()) {
            FileMetadata file = files.get(0); // 첫 번째 파일만 대표로 사용
            fileDto = new FileMetadataDto(file, bucketName);
        }
        return new MemberStudyContext(member, fileDto);
    }

    /**
     * username과 StudyId에 해당하는 Member 조회
     */
    public Optional<Member> findMemberByUsernameAndStudyId(String username, Long studyId) {
        Student student = studentRepository.findByUsername(username)
                .orElseThrow(() -> new CustomException(STUDENT_NOT_FOUND));
        Long studentId = student.getStudentId();

        return memberRepository.findByStudentStudentIdAndStudyStudyId(studentId, studyId);
    }

    /**
     * 사용자가 특정 스터디에 속해 있는지 확인
     */
    public boolean isMemberInStudy(String username, Long studyId) {
        Student student = studentRepository.findByUsername(username)
                .orElseThrow(() -> new CustomException(STUDENT_NOT_FOUND));

        Long studentId = student.getStudentId();
        return memberRepository.findByStudentStudentIdAndStudyStudyId(studentId, studyId).isPresent();
    }

    /**
     * 사용자가 특정 스터디에서 HOST 또는 CREATOR 역할을 가지고 있는지 확인
     */
    public boolean isMemberCreatorOrHost(String username, Long studyId) {
        Student student = studentRepository.findByUsername(username)
                .orElseThrow(() -> new CustomException(STUDENT_NOT_FOUND));

        Long studentId = student.getStudentId();
        return memberRepository.findByStudentStudentIdAndStudyStudyId(studentId, studyId)
                .map(member ->
                        member.getMemberRole().equals(MemberRole.HOST) ||
                                member.getMemberRole().equals(MemberRole.CREATOR))
                .orElse(false);
    }

    /**
     *  스터디에 속한 멤버 목록 조회
     */
    public ContextResponse<MemberManagementResponse> getMembersForStudy(String username, Long studyId) {

        // CREATOR, HOST 만 가능
        if (!isMemberCreatorOrHost(username, studyId)) {
            throw new CustomException(ACCESS_DENIED);
        }

        List<Member> members = memberRepository.findByStudyStudyId(studyId);
        List<MemberListResponse> memberListResponse = members.stream()
                .map(MemberListResponse::from)
                .toList();

        // studyName 은 member 중 하나에서 꺼냄 (어차피 같은 스터디이므로)
        String studyName = members.isEmpty() ? null : members.get(0).getStudy().getStudyName();

        // 실제 응답 객체 생성
        MemberManagementResponse response = new MemberManagementResponse(studyName, memberListResponse);

        MemberStudyContext context = getContext(username, studyId);
        return new ContextResponse<>(context, response);
    }

    @Transactional
    public void addMemberToStudy(String username, Long studyId, AddMemberRequest request) {

        // CREATOR, HOST 만 가능
        if (!isMemberCreatorOrHost(username, studyId)) {
            throw new CustomException(ACCESS_DENIED);
        }

        Study study = studyRepository.findById(studyId)
                .orElseThrow(() -> new CustomException(STUDY_NOT_FOUND));

        Student student = studentRepository.findByStudentEmail(request.getStudentEmail())
                .orElseThrow(() -> new CustomException(STUDENT_NOT_FOUND));

        Member newMember = new Member(student, study, MemberRole.PARTICIPANT, LocalDate.now());
        memberRepository.save(newMember);

        // 1. 알림 생성
        notificationService.createNotificationForNewMember(studyId, student.getStudentId());

        // 2. 기존 Schedule 에 대해 Attendance 생성
        attendanceService.createAttendancesForMember(newMember);

        // 3. 기존 Assignment 에 대해 Submission 생성
        List<Assignment> assignments = assignmentRepository.findByStudyStudyId(newMember.getStudy().getStudyId());
        for (Assignment assignment : assignments) {
            Submission submission = Submission.builder()
                    .assignment(assignment)
                    .member(newMember)
                    .submissionContent("")
                    .submissionStatus(SubmissionStatus.NOTSUBMITTED)
                    .submissionCreatedAt(LocalDateTime.now())
                    .submissionComment(null)
                    .submissionScore(null)
                    .build();

            submissionRepository.save(submission);
        }
    }

    @Transactional
    public void updateMemberRole(String username, Long studyId, Long memberId, String newRole) {

        // CREATOR, HOST 만 가능
        if (!isMemberCreatorOrHost(username, studyId)) {
            throw new CustomException(ACCESS_DENIED);
        }

        Member member = memberRepository.findByMemberIdAndStudyStudyId(memberId, studyId)
                .orElseThrow(() -> new CustomException(MEMBER_NOT_FOUND));

        // CREATOR 는 권한 변경 불가
        if (member.getMemberRole() == MemberRole.CREATOR) {
            throw new CustomException(INVALID_ROLE_CHANGE);
        }
        member.changeRole(MemberRole.valueOf(newRole));
    }


    @Transactional
    public void deleteMember(String username, Long studyId, Long memberIdToDelete, String usernameOfRequester) {

        // CREATOR, HOST 만 가능
        if (!isMemberCreatorOrHost(username, studyId)) {
            throw new CustomException(ACCESS_DENIED);
        }

        // 삭제할 멤버 조회
        Member targetMember = memberRepository.findByMemberIdAndStudyStudyId(memberIdToDelete, studyId)
                .orElseThrow(() -> new CustomException(MEMBER_NOT_FOUND));

        // 요청자 본인의 멤버 객체 조회
        Member loginedMember = findMemberByUsernameAndStudyId(usernameOfRequester, studyId)
                .orElseThrow(() -> new CustomException(MEMBER_NOT_FOUND));

        // 해당 스터디에 존재하던 알림 삭제
        notificationService.deleteNotificationByMember(
                targetMember.getStudent().getStudentId(), targetMember.getStudy().getStudyId());

        // 자기 자신 삭제 방지
        if (targetMember.getMemberId().equals(loginedMember.getMemberId())) {
            throw new CustomException(INVALID_REQUEST);
        }

        // HOST는 삭제 불가
        if (targetMember.getMemberRole().equals(MemberRole.HOST)||
                targetMember.getMemberRole().equals(MemberRole.CREATOR)) {
            throw new CustomException(INVALID_MEMBER_DELETION);
        }
        memberRepository.delete(targetMember);
    }
}