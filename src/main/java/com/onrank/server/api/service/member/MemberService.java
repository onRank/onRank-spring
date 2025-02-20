package com.onrank.server.api.service.member;

import com.onrank.server.api.service.student.StudentService;
import com.onrank.server.domain.member.Member;
import com.onrank.server.domain.member.MemberJpaRepository;
import com.onrank.server.domain.member.MemberRole;
import com.onrank.server.domain.student.Student;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MemberService {

    private final MemberJpaRepository memberRepository;
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
}
