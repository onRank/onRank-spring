package com.onrank.server.domain.student;

import com.onrank.server.domain.member.Member;
import jakarta.persistence.*;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
public class Student {

    @Id
    @GeneratedValue
    private Long studentId;

    @Column(nullable = false)
    private String studentName;

    @Column(nullable = false)
    private String studentEmail;

    @Column(nullable = false)
    private String department;

    @Column(nullable = false)
    private String phoneNumber;

    @OneToMany(mappedBy = "student")
    private List<Member> members = new ArrayList<>();
}
