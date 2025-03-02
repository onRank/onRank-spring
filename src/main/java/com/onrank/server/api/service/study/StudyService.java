package com.onrank.server.api.service.study;

import com.onrank.server.domain.member.Member;
import com.onrank.server.domain.member.MemberJpaRepository;
import com.onrank.server.domain.student.Student;
import com.onrank.server.domain.student.StudentJpaRepository;
import com.onrank.server.domain.study.Study;
import com.onrank.server.domain.study.StudyJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class StudyService {

    private final StudentJpaRepository studentRepository;
    private final StudyJpaRepository studyRepository;

    public Optional<Study> findByStudyId(Long id) {
        return studyRepository.findByStudyId(id);
    }

    public List<Study> getStudiesByUsername(String username) {

        Student student = studentRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Student not found"));

        List<Member> members = student.getMembers();
        List<Study> studies = new ArrayList<>();
        for (Member member : members) {
            studies.add(member.getStudy());
        }

        return studies;
    }
}
