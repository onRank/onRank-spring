package com.onrank.server.domain.submission;

import com.onrank.server.domain.assignment.Assignment;
import com.onrank.server.domain.member.Member;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

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
    private String submissionContent = "";

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private SubmissionStatus submissionStatus = SubmissionStatus.NOTSUBMITTED; // 기본값 설정

    @Column(nullable = false)
    private LocalDateTime submissionCreatedAt;

    private Integer submissionScore = null;
    private String submissionComment = null;

    @Builder
    public Submission(Assignment assignment, Member member, String submissionContent, SubmissionStatus submissionStatus, LocalDateTime submissionCreatedAt, Integer submissionScore, String submissionComment) {
        this.assignment = assignment;
        this.member = member;
        this.submissionContent = submissionContent;
        this.submissionStatus = submissionStatus;
        this.submissionCreatedAt = submissionCreatedAt;
        this.submissionScore = submissionScore;
        this.submissionComment = submissionComment;
    }

    // 과제 제출 업데이트 메서드
    public void updateSubmission(String submissionContent, LocalDateTime submissionCreatedAt) {
        this.submissionContent = submissionContent;
        this.submissionStatus = SubmissionStatus.SUBMITTED;
        this.submissionCreatedAt = LocalDateTime.now();
    }

    // 점수와 코멘트 업데이트 메서드
    public void updateScore(int score, String comment) {
        this.submissionScore = score;
        this.submissionComment = comment;
        this.submissionStatus = SubmissionStatus.SCORED;
    }
}
