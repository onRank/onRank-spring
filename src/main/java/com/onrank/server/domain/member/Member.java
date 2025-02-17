package com.onrank.server.domain.member;

import com.onrank.server.domain.student.Student;
import com.onrank.server.domain.study.Study;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "members")
public class Member {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "member_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_id", nullable = false)
    private Student student;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "study_id", nullable = false)
    private Study study;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private MemberRole role;

    @Column(nullable = false)
    private LocalDate joiningAt;

    // 생성자
    @Builder
    public Member(Student student, Study study, MemberRole role, LocalDate joiningAt) {
        // 연관관계 설정
        setStudent(student);
        setStudy(study);

        this.role = role;
        this.joiningAt = joiningAt;
    }

    //==연관관계 메서드==//
    public void setStudent(Student student) {
        if (this.student != null) {
            this.student.getMembers().remove(this);
        }
        this.student = student;

        if (student != null) {
            student.getMembers().add(this);
        }
    }

    public void setStudy(Study study) {
        if (this.study != null) {
            this.study.getMembers().remove(this);
        }
        this.study = study;

        if (study != null) {
            study.getMembers().add(this);
        }
    }

    //==비지니스 로직==//
    public void changeRole(MemberRole memberRole) {
        this.role = memberRole;
    }
}
