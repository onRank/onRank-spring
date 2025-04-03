package com.onrank.server.api.service.member;

import com.onrank.server.api.dto.member.AddMemberRequest;
import com.onrank.server.api.dto.member.MemberListResponse;
import com.onrank.server.api.dto.member.MemberResponse;
import com.onrank.server.api.dto.member.MemberRoleResponse;
import com.onrank.server.api.service.student.StudentService;
import com.onrank.server.domain.member.Member;
import com.onrank.server.domain.member.MemberJpaRepository;
import com.onrank.server.domain.member.MemberRole;
import com.onrank.server.domain.student.Student;
import com.onrank.server.domain.study.Study;
import com.onrank.server.domain.study.StudyJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;


@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MemberService {

    private final MemberJpaRepository memberRepository;
    private final StudyJpaRepository studyRepository;
    private final StudentService studentService;

    public MemberRoleResponse getMyRoleInStudy(String username, Long studyId) {
        Member member = findByUsernameAndStudyId(username, studyId)
                .orElseThrow(() -> new IllegalArgumentException("Member not found"));
        return new MemberRoleResponse(member);
    }
    /**
     * username과 StudyId에 해당하는 Member 조회
     */
    public Optional<Member> findByUsernameAndStudyId(String username, Long studyId) {
        Student student = studentService.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("Student not found"));
        Long studentId = student.getStudentId();
        
        return memberRepository.findByStudentStudentIdAndStudyStudyId(studentId, studyId);
    }

    /**
     * 사용자가 특정 스터디에 속해 있는지 확인
     */
    public boolean isMemberInStudy(String username, Long studyId) {
        Student student = studentService.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("Student not found"));

        Long studentId = student.getStudentId();
        return memberRepository.findByStudentStudentIdAndStudyStudyId(studentId, studyId).isPresent();
    }

    /**
     * 사용자가 특정 스터디에서 HOST 또는 CEREATER 역할을 가지고 있는지 확인
     */
    public boolean isMemberCreaterOrHost(String username, Long studyId) {
        Student student = studentService.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("Student not found"));

        Long studentId = student.getStudentId();
        return memberRepository.findByStudentStudentIdAndStudyStudyId(studentId, studyId)
                .map(member ->
                        member.getMemberRole().equals(MemberRole.HOST) ||
                        member.getMemberRole().equals(MemberRole.CREATER))
                .orElse(false);
    }

    /**
     *  스터디에 속한 멤버 목록 조회
     */
    public MemberListResponse getMembersForStudy(Long studyId) {

        List<Member> members = memberRepository.findByStudyStudyId(studyId);
        List<MemberResponse> memberResponses = members.stream()
                .map(MemberResponse::new)
                .toList();

        // studyName은 member 중 하나에서 꺼냄 (어차피 같은 스터디이므로)
        String studyName = members.isEmpty() ? null : members.get(0).getStudy().getStudyName();

        return MemberListResponse.builder()
                .StudyName(studyName)
                .members(memberResponses)
                .build();
    }

    @Transactional
    public void addMemberToStudy(Long studyId, AddMemberRequest request) {

        Study study = studyRepository.findById(studyId)
                .orElseThrow(() -> new IllegalArgumentException("Study not found with id: " + studyId));

        Student student = studentService.findByStudentEmail(request.getStudentEmail())
                .orElseThrow(() -> new IllegalArgumentException("Student not found with email: " + request.getStudentEmail()));

        Member newMember = new Member(student, study, MemberRole.PARTICIPANT, LocalDate.now());
        memberRepository.save(newMember);
    }

    @Transactional
    public void updateMemberRole(Long studyId, Long memberId, String newRole) {
        Member member = memberRepository.findByMemberIdAndStudyStudyId(memberId, studyId)
                .orElseThrow(() -> new IllegalArgumentException("Member not in Study"));

        // CREATER는 권한 변경 불가
        if (member.getMemberRole() == MemberRole.CREATER) {
            throw new IllegalStateException("CREATER는 권한을 수정할 수 없습니다.");
        }
        member.changeRole(MemberRole.valueOf(newRole));
    }


    @Transactional
    public void deleteMember(Long studyId, Long memberIdToDelete, String usernameOfRequester) {
        // 삭제할 멤버 조회
        Member targetMember = memberRepository.findByMemberIdAndStudyStudyId(memberIdToDelete, studyId)
                .orElseThrow(() -> new IllegalArgumentException("해당 스터디에 멤버가 없습니다."));

        // 요청자 본인의 멤버 객체 조회
        Member requester = findByUsernameAndStudyId(usernameOfRequester, studyId)
                .orElseThrow(() -> new IllegalArgumentException("해당 스터디에 소속되지 않은 사용자입니다."));

        // 자기 자신 삭제 방지
        if (targetMember.getMemberId().equals(requester.getMemberId())) {
            throw new IllegalArgumentException("자기 자신은 삭제할 수 없습니다.");
        }

        // HOST는 삭제 불가
        if (targetMember.getMemberRole().equals(MemberRole.HOST)) {
            throw new IllegalArgumentException("HOST는 삭제할 수 없습니다.");
        }
        memberRepository.delete(targetMember);
    }

}
