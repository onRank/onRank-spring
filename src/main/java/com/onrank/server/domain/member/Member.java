package com.onrank.server.domain.member;

import com.onrank.server.domain.attendance.Attendance;
import com.onrank.server.domain.attendance.AttendanceStatus;
import com.onrank.server.domain.post.Post;
import com.onrank.server.domain.student.Student;
import com.onrank.server.domain.study.Study;
import com.onrank.server.domain.submission.Submission;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "members")
public class Member {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long memberId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_id", nullable = false)
    private Student student;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "study_id", nullable = false)
    private Study study;

    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Post> posts = new ArrayList<>();

    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Attendance> attendances = new ArrayList<>();

    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Submission> submissions = new ArrayList<>();

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private MemberRole memberRole;

    @Column(nullable = false)
    private LocalDate memberJoiningAt;

    @Column(nullable = false)
    private Long memberSubmissionPoint = 0L;

    @Column(nullable = false)
    private Long memberPresentCount = 0L;

    @Column(nullable = false)
    private Long memberLateCount = 0L;

    @Column(nullable = false)
    private Long memberAbsentCount = 0L;

    @Column(length = 7, nullable = false)
    private String memberColorCode = generateRandomColorCode();


    // 생성자
    @Builder
    public Member(Student student, Study study, MemberRole memberRole, LocalDate memberJoiningAt) {
        this.study = study;
        this.student = student;
        this.memberRole = memberRole;
        this.memberJoiningAt = memberJoiningAt;
    }

    //==비지니스 로직==//
    public void changeRole(MemberRole memberRole) {
        this.memberRole = memberRole;
    }

    public void changeSubmissionPoint(Long submissionPoint) {
        this.memberSubmissionPoint = submissionPoint;
    }

    public void updateAttendanceCount(AttendanceStatus oldStatus, AttendanceStatus newStatus) {
        switch (oldStatus) {
            case PRESENT -> this.memberPresentCount--;
            case LATE -> this.memberLateCount--;
            case ABSENT -> this.memberAbsentCount--;
        }

        switch (newStatus) {
            case PRESENT -> this.memberPresentCount++;
            case LATE -> this.memberLateCount++;
            case ABSENT -> this.memberAbsentCount++;
        }
    }

    private String generateRandomColorCode() {
        String[] colorPool = {
                "#9876A8", "#F288A4", "#F20F0F",
                "#CBDBA7", "#03A65A", "#00BDAD",
                "#F2E2CE", "#F2CB05", "#F26938",
                "#049DBF", "#599BBB", "#2D55A6"
        };
        return colorPool[(int) (Math.random() * colorPool.length)];
    }
}
