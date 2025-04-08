package com.onrank.server.domain.assignment;

import com.onrank.server.domain.study.Study;
import com.onrank.server.domain.submission.Submission;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "assignments")
public class Assignment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long assignmentId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "study_id", nullable = false)
    private Study study;

    @OneToMany(mappedBy = "assignment", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Submission> submissions = new ArrayList<>();

    @Column(nullable = false)
    private String assignmentTitle;

    @Column(nullable = false)
    private String assignmentContent;

    @Column(nullable = false)
    private LocalDate assignmentCreatedAt;

    @Column(nullable = false)
    private LocalDateTime assignmentDueDate;

    @Builder
    public Assignment(Study study, String assignmentTitle, String assignmentContent, LocalDate assignmentCreatedAt, LocalDateTime assignmentDueDate) {
        this.study = study;
        this.assignmentTitle = assignmentTitle;
        this.assignmentContent = assignmentContent;
        this.assignmentCreatedAt = assignmentCreatedAt;
        this.assignmentDueDate = assignmentDueDate;
    }

    /**
     * 과제 수정 메서드
     */
    public void update(String assignmentTitle, String assignmentContent, LocalDateTime assignmentDueDate) {
        this.assignmentTitle = assignmentTitle;
        this.assignmentContent = assignmentContent;
        this.assignmentDueDate = assignmentDueDate;
    }
}
