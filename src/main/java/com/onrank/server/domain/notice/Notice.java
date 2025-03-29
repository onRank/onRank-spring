package com.onrank.server.domain.notice;

import com.onrank.server.domain.study.Study;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "notices")
public class Notice {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long noticeId;

    @Column(nullable = false)
    private String noticeTitle;

    @Column(nullable = false)
    private String noticeContent;

    @Column(nullable = false)
    private LocalDate noticeCreatedAt;

    @Column(nullable = false)
    private LocalDate noticeModifiedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "study_id")
    private Study study;

    @Builder
    public Notice(Long noticeId, String noticeTitle, String noticeContent, LocalDate noticeCreatedAt, LocalDate noticeModifiedAt, Study study) {
        this.noticeId = noticeId;
        this.noticeTitle = noticeTitle;
        this.noticeContent = noticeContent;
        this.noticeCreatedAt = noticeCreatedAt;
        this.noticeModifiedAt = noticeModifiedAt;
        this.study = study;
    }

    /**
     * 공지사항 수정 메서드
     */
    public void update(String noticeTitle, String noticeContent) {
        this.noticeTitle = noticeTitle;
        this.noticeContent = noticeContent;
        this.noticeModifiedAt = LocalDate.now();
    }
}
