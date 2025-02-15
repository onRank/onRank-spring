package com.onrank.server.domain.student;

import com.onrank.server.domain.member.Member;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "students")
public class Student {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "student_id")
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false, unique = true)
    private String email;

    private String school;
    private String department;

    @Column(nullable = false)
    private String phoneNumber;

    @OneToMany(mappedBy = "student", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Member> members = new ArrayList<>();

    // 생성자
    @Builder
    public Student(String name, String email, String school, String department, String phoneNumber) {
        this.name = name;
        this.email = email;
        this.school = school;
        this.department = department;
        this.phoneNumber = phoneNumber;
    }
}
