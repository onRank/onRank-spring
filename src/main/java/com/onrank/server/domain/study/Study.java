package com.onrank.server.domain.study;

import com.onrank.server.domain.assignment.Assignment;
import com.onrank.server.domain.member.Member;
import com.onrank.server.domain.notice.Notice;
import com.onrank.server.domain.notification.Notification;
import com.onrank.server.domain.schedule.Schedule;
import jakarta.persistence.*;
import lombok.*;

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

    // Study와 Member의 1:N 관계 설정
    @OneToMany(mappedBy = "study", cascade = CascadeType.REMOVE, orphanRemoval = true)
    private List<Member> members = new ArrayList<>();

    // Study와 Notice의 1:N 관계 설정
    @OneToMany(mappedBy = "study", cascade = CascadeType.REMOVE, orphanRemoval = true)
    private List<Notice> notices = new ArrayList<>();

    // Study와 Schedule 1:N 관계 설정
    @OneToMany(mappedBy = "study", cascade = CascadeType.REMOVE, orphanRemoval = true)
    private List<Schedule> schedules = new ArrayList<>();

    @OneToMany(mappedBy = "study", cascade = CascadeType.REMOVE, orphanRemoval = true)
    private List<Assignment> assignments = new ArrayList<>();

    @OneToMany(mappedBy = "study", cascade = CascadeType.REMOVE, orphanRemoval = true)
    private List<Notification> notifications = new ArrayList<>();

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
}