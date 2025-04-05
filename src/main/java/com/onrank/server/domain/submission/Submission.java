package com.onrank.server.domain.submission;

import com.onrank.server.domain.assignment.Assignment;
import com.onrank.server.domain.member.Member;
import com.onrank.server.domain.student.Student;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;


@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "submissions")
public class Submission {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long submissionId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "assignment_id", nullable = false)
    private Assignment assignment;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    @Column(nullable = false)
    private String submissionContent;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private SubmissionStatus submissionStatus = SubmissionStatus.NOTSUBMITTED; // 기본값 설정

    @Column(nullable = false)
    private LocalDateTime submissionCreatedAt;

    private int submissionScore;
    private String submissionComment;

    // 과제 제출 업데이트 메서드
    public void update(String submissionContent, LocalDateTime submissionCreatedAt) {
        this.submissionContent = submissionContent;
        this.submissionCreatedAt = LocalDateTime.now();
    }

    // 점수와 코멘트 업데이트 메서드
    public void updateScore(int score, String comment) {
        this.submissionScore = score;
        this.submissionComment = comment;
    }
}
