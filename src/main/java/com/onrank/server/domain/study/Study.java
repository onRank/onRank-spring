package com.onrank.server.domain.study;

import com.onrank.server.domain.assignment.Assignment;
import com.onrank.server.domain.member.Member;
import com.onrank.server.domain.notice.Notice;
import com.onrank.server.domain.post.Post;
import com.onrank.server.domain.schedule.Schedule;
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
@Table(name = "studies")
public class Study {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long studyId;

    @Column(nullable = false)
    private String studyName;

    @Column(nullable = false)
    private String studyContent;

    // 스터디 출석 point
    private int presentPoint;
    private int absentPoint;
    private int latePoint;

    // Study 와 Member 1:N 관계 설정
    @OneToMany(mappedBy = "study", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Member> members = new ArrayList<>();

    // Study 와 Notice 1:N 관계 설정
    @OneToMany(mappedBy = "study", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Notice> notices = new ArrayList<>();

    // Study 와 POST 1:N 관계 설정
    @OneToMany(mappedBy = "study", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Post> posts = new ArrayList<>();

    // Study 와 Schedule 1:N 관계 설정
    @OneToMany(mappedBy = "study", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Schedule> schedules = new ArrayList<>();

    // Study 와 Assignment 1:N 관계 설정
    @OneToMany(mappedBy = "study", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Assignment> assignments = new ArrayList<>();

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StudyStatus studyStatus = StudyStatus.PROGRESS; // 기본값 설정

    // 생성자
    @Builder
    public Study(String studyName, String studyContent, int presentPoint, int absentPoint, int latePoint) {
        this.studyName = studyName;
        this.studyContent = studyContent;
        this.presentPoint = presentPoint;
        this.absentPoint = absentPoint;
        this.latePoint = latePoint;
    }

    /**
     * 스터디 관리 - 수정 메서드
     */
    public void update(String studyName, String studyContent, int presentPoint, int absentPoint, int latePoint, StudyStatus studyStatus) {
        this.studyName = studyName;
        this.studyContent = studyContent;
        this.presentPoint = presentPoint;
        this.absentPoint = absentPoint;
        this.latePoint = latePoint;
        this.studyStatus = studyStatus;
    }

    public void clearAllRelations() {
        this.notices.clear();
        this.posts.clear();
        this.assignments.clear();
        this.schedules.clear();
        this.members.clear();
    }
}