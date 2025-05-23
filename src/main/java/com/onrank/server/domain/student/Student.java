package com.onrank.server.domain.student;

import com.onrank.server.domain.member.Member;
import com.onrank.server.domain.notification.Notification;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "students")
public class Student {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long studentId;

    @Column(nullable = false)
    private String studentName;

    private String studentSchool;
    private String studentDepartment;

    @Column(nullable = false)
    private String studentPhoneNumber;

    private String username;

    @Column(nullable = false, unique = true)
    private String studentEmail;

    @ElementCollection(fetch = FetchType.EAGER)
    @Enumerated(EnumType.STRING)
    private Set<Role> roles = new HashSet<>();

    @OneToMany(mappedBy = "student", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Member> members = new ArrayList<>();

    @OneToMany(mappedBy = "student", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Notification> notifications = new ArrayList<>();

    // 생성자
    @Builder
    public Student(String studentName, String studentEmail, String studentSchool, String studentDepartment, String studentPhoneNumber, Set<Role> roles, String username) {
        this.studentName = studentName;
        this.studentEmail = studentEmail;
        this.studentSchool = studentSchool;
        this.studentDepartment = studentDepartment;
        this.studentPhoneNumber = studentPhoneNumber;
        this.roles = roles;
        this.username = username;
    }

    public void update(String name, String school, String department, String phoneNumber) {
        this.studentName = name;
        this.studentSchool = school;
        this.studentDepartment = department;
        this.studentPhoneNumber = phoneNumber;
    }
}
