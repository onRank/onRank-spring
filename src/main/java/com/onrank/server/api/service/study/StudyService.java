package com.onrank.server.api.service.study;

import com.onrank.server.api.dto.attendance.AttendancePointRequest;
import com.onrank.server.api.dto.attendance.AttendancePointResponse;
import com.onrank.server.api.dto.common.ContextResponse;
import com.onrank.server.api.dto.file.FileMetadataDto;
import com.onrank.server.api.dto.common.MemberStudyContext;
import com.onrank.server.api.dto.file.PresignedUrlResponse;
import com.onrank.server.api.dto.member.MemberPointDto;
import com.onrank.server.api.dto.study.*;
import com.onrank.server.api.service.file.FileService;
import com.onrank.server.api.service.member.MemberService;
import com.onrank.server.common.exception.CustomException;
import com.onrank.server.domain.assignment.AssignmentJpaRepository;
import com.onrank.server.domain.file.FileCategory;
import com.onrank.server.domain.file.FileMetadata;
import com.onrank.server.domain.file.FileMetadataJpaRepository;
import com.onrank.server.domain.member.Member;
import com.onrank.server.domain.member.MemberJpaRepository;
import com.onrank.server.domain.member.MemberRole;  // 이 줄 추가
import com.onrank.server.domain.notice.NoticeJpaRepository;
import com.onrank.server.domain.post.PostJpaRepository;
import com.onrank.server.domain.student.Student;
import com.onrank.server.domain.student.StudentJpaRepository;
import com.onrank.server.domain.study.Study;
import com.onrank.server.domain.study.StudyJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDate;  // 이 줄도 필요합니다
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

import static com.onrank.server.common.exception.CustomErrorInfo.*;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class StudyService {

    private final StudentJpaRepository studentRepository;
    private final StudyJpaRepository studyRepository;
    private final MemberJpaRepository memberJpaRepository;
    private final MemberService memberService;
    private final FileService fileService;
    private final FileMetadataJpaRepository fileMetadataRepository;
    private final NoticeJpaRepository noticeRepository;
    private final PostJpaRepository postRepository;
    private final AssignmentJpaRepository assignmentRepository;
    private final MemberJpaRepository memberRepository;

    public Optional<Study> findByStudyId(Long id) {

        return studyRepository.findByStudyId(id);
    }


    @Transactional
    public AddStudyResponse createStudy(AddStudyRequest addStudyRequest, String username) {

        Study study = studyRepository.save(addStudyRequest.toEntity());

        // url 추가
        String presignedUrl = fileService.createPresignedUrlAndSaveMetadata(FileCategory.STUDY, study.getStudyId(), addStudyRequest.getFileName());

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

    public ContextResponse<StudyDetailResponse> getStudyDetail(String username, Long studyId) {
        Study study = studyRepository.findById(studyId)
                .orElseThrow(() -> new IllegalArgumentException("해당 스터디가 존재하지 않습니다."));

        List<FileMetadata> files = fileMetadataRepository
                .findByCategoryAndEntityId(FileCategory.STUDY, study.getStudyId());

        FileMetadataDto fileDto = null;
        if (!files.isEmpty()) {
            FileMetadata file = files.get(0); // 첫 번째 파일만 대표로 사용
            fileDto = new FileMetadataDto(file, "onrank-bucket");
        }

        MemberStudyContext memberContext = memberService.getContext(username, studyId);
        StudyDetailResponse detail = new StudyDetailResponse(study);

        return new ContextResponse<>(memberContext, detail);
    }

    @Transactional
    public ContextResponse<PresignedUrlResponse> updateStudy(String username, Long studyId, StudyUpdateRequest request) {
        // CREATOR, HOST 만 가능
        if (!memberService.isMemberCreatorOrHost(username, studyId)) {
            throw new CustomException(ACCESS_DENIED);
        }

        // 스터디 정보 수정
        Study study = studyRepository.findById(studyId)
                .orElseThrow(() -> new IllegalArgumentException("Study not found"));
        study.update(request.getStudyName(), request.getStudyContent(), request.getPresentPoint(), request.getAbsentPoint(), request.getLatePoint(), request.getStudyStatus());

        // 스터디 파일(이미지) 수정 - newFileName 존재시
        PresignedUrlResponse response = null;
        if(StringUtils.hasText(request.getNewFileName())) {
            response = fileService.replaceStudyFile(studyId, request.getNewFileName());
        }

        // 멤버 권한 포함
        MemberStudyContext memberContext = memberService.getContext(username, studyId);
        return new ContextResponse<>(memberContext, response);
    }

    public ContextResponse<AttendancePointResponse> getAttendancePoint(Long studyId, String username) {
        // 스터디 멤버만 가능
        if (!memberService.isMemberInStudy(username, studyId)) {
            throw new CustomException(NOT_STUDY_MEMBER);
        }

        Study study = studyRepository.findByStudyId(studyId)
                .orElseThrow(() -> new IllegalArgumentException("Study not found"));

        AttendancePointResponse response = new AttendancePointResponse(
                study.getPresentPoint(),
                study.getAbsentPoint(),
                study.getLatePoint()
        );
        MemberStudyContext memberContext = memberService.getContext(username, studyId);
        return new ContextResponse<>(memberContext, response);
    }

    @Transactional
    public void updateAttendancePoint(String username, Long studyId, AttendancePointRequest request) {

        // CREATOR, HOST 만 가능
        if (!memberService.isMemberCreatorOrHost(username, studyId)) {
            throw new CustomException(ACCESS_DENIED);
        }

        Study study = studyRepository.findByStudyId(studyId)
                .orElseThrow(() -> new IllegalArgumentException("Study not found"));

        // 기존 정보 유지 (나머지는 그대로 두고 포인트만 갱신)
        study.update(
                study.getStudyName(),
                study.getStudyContent(),
                request.getPresentPoint(),
                request.getAbsentPoint(),
                request.getLatePoint(),
                study.getStudyStatus()
        );
    }

    @Transactional
    public void deleteStudy(String username, Long studyId) {

        // CREATOR, HOST 만 가능
        if (!memberService.isMemberCreatorOrHost(username, studyId)) {
            throw new CustomException(ACCESS_DENIED);
        }

        // 1. 공지사항 파일 삭제
        noticeRepository.findByStudyStudyId(studyId)
                .forEach(notice -> fileService.deleteAllFilesAndMetadata(FileCategory.NOTICE, notice.getNoticeId()));

        // 2. 게시글 파일 삭제
        postRepository.findByStudyStudyId(studyId)
                .forEach(post -> fileService.deleteAllFilesAndMetadata(FileCategory.POST, post.getPostId()));

        // 3. 과제 파일 삭제
        assignmentRepository.findByStudyStudyId(studyId)
                .forEach(assignment -> fileService.deleteAllFilesAndMetadata(FileCategory.ASSIGNMENT, assignment.getAssignmentId()));

        // 4. 스터디 파일 S3 및 FileMetadata 삭제
        fileService.deleteAllFilesAndMetadata(FileCategory.STUDY, studyId);

        // 5. 스터디 삭제(cascade 또는 OrphanRemoval 로 연결된 엔티티 자동 삭제)
        studyRepository.deleteById(studyId);
    }

    public ContextResponse<StudyPageResponse> getStudyPage(String username, Long studyId) {

        // 스터디 멤버만 가능
        if (!memberService.isMemberInStudy(username, studyId)) {
            throw new CustomException(NOT_STUDY_MEMBER);
        }

        List<Member> members = memberRepository.findByStudyStudyId(studyId);
        Member loginMember = memberService.findMemberByUsernameAndStudyId(username, studyId)
                .orElseThrow(() -> new CustomException(MEMBER_NOT_FOUND));
        Study study = studyRepository.findByStudyId(studyId)
                .orElseThrow(() -> new CustomException(STUDY_NOT_FOUND));

        // 점수 기준
        int StudyPresentPoint = study.getPresentPoint();
        int StudyAbsentPoint = study.getAbsentPoint();
        int StudyLatePoint = study.getLatePoint();

        // 전체 랭킹용 DTO 목록 (List<MemberPointDto> memberPointList 생성)
        List<MemberPointDto> memberPointDtos = members.stream()
                .map(member -> {
                    Long totalPoint = member.getMemberPresentCount() * StudyPresentPoint
                            + member.getMemberAbsentCount() * StudyAbsentPoint
                            + member.getMemberLateCount() * StudyLatePoint
                            + member.getMemberSubmissionPoint();

                    return MemberPointDto.builder()
                            .studentName(member.getStudent().getStudentName())
                            .memberId(member.getMemberId())
                            .totalPoint(totalPoint)
                            .build();
                })
                .sorted(Comparator.comparingLong(MemberPointDto::totalPoint).reversed())
                .toList();

        // 본인 점수 계산
        Long memberPresentPoint = loginMember.getMemberPresentCount() * StudyPresentPoint;
        Long memberLatePoint = loginMember.getMemberLateCount() * StudyLatePoint;
        Long memberAbsentPoint = loginMember.getMemberAbsentCount() * StudyAbsentPoint;
        Long memberSubmissionPoint = loginMember.getMemberSubmissionPoint();

        // 응답 Dto 완성
        StudyPageResponse response = StudyPageResponse.builder()
                .studyId(studyId)
                .memberId(loginMember.getMemberId())
                .memberSubmissionPoint(memberSubmissionPoint)
                .memberPresentPoint(memberPresentPoint)
                .memberLatePoint(memberLatePoint)
                .memberAbsentPoint(memberAbsentPoint)
                .memberPointList(memberPointDtos)
                .build();
        MemberStudyContext memberContext = memberService.getContext(username, studyId);
        return new ContextResponse<>(memberContext, response);
    }
}
