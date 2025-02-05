package com.onrank.server.domain.member;

import com.onrank.server.domain.student.Student;
import jakarta.persistence.*;
import lombok.Getter;

import java.time.LocalDate;

@Entity
@Getter
public class Member {

    @Id
    @GeneratedValue
    private Long memberId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_id")
    private Student student;

    @Enumerated(EnumType.STRING)
    private MemberRole memberRole;

    @Column(nullable = false)
    private LocalDate joiningAt;

    //==연관관계 메서드==//
    public void setStudent(Student student) {
        this.student = student;
        student.getMembers().add(this);
    }

    //==비지니스 로직==//
    public void changeRole(MemberRole memberRole) {
        this.memberRole = memberRole;
    }
}
