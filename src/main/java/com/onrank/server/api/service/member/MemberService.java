package com.onrank.server.api.service.member;

import com.onrank.server.api.dto.member.AddMemberRequestDto;
import com.onrank.server.api.dto.member.MemberResponseDto;
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
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MemberService {

    private final MemberJpaRepository memberRepository;
    private final StudyJpaRepository studyRepository;
    private final StudentService studentService;

    /**
     * 사용자가 특정 스터디에 속해 있는지 확인
     */
    public boolean isMemberInStudy(String username, Long studyId) {
        Student student = studentService.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("Student를 찾을 수 없습니다."));

        Long studentId = student.getStudentId();
        return memberRepository.findByStudentStudentIdAndStudyStudyId(studentId, studyId).isPresent();
    }

    /**
     * 사용자가 특정 스터디에서 HOST 역할을 가지고 있는지 확인
     */
    public boolean isMemberHost(String username, Long studyId) {
        Student student = studentService.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("Student를 찾을 수 없습니다."));

        Long studentId = student.getStudentId();
        return memberRepository.findByStudentStudentIdAndStudyStudyId(studentId, studyId)
                .map(member -> member.getMemberRole().equals(MemberRole.HOST))
                .orElse(false);
    }

    /**
     *  DB에서 스터디에 속한 멤버 목록 조회
     */
    public List<MemberResponseDto> getMembersForStudy(Long studyId) {

        List<Member> members = memberRepository.findAllByStudy_StudyId(studyId);

        return members.stream()
                .map(member -> new MemberResponseDto(
                        member.getStudent().getStudentName(),
                        member.getStudent().getStudentEmail()
                ))
                .collect(Collectors.toList());
    }

    @Transactional
    public Member addMemberToStudy(Long studyId, AddMemberRequestDto requestDto) {

        Study study = studyRepository.findById(studyId)
                .orElseThrow(() -> new IllegalArgumentException("Study not found with id: " + studyId));

        Student student = studentService.findByStudentEmail(requestDto.getStudentEmail())
                .orElseThrow(() -> new IllegalArgumentException("Student not found with email: " + requestDto.getStudentEmail()));

        Member newMember = new Member(student, study, MemberRole.PARTICIPANT, LocalDate.now());

        // 3. DB에 저장
        return memberRepository.save(newMember);
    }
}
