package com.onrank.server.domain.notification;

import com.onrank.server.domain.student.Student;
import com.onrank.server.domain.study.Study;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "notifications")
public class Notification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long notificationId;

    @Enumerated(EnumType.STRING)
    private NotificationCategory notificationCategory;

    @Column(nullable = false)
    private Long entityId;

    private String fileKey;

    @Column(nullable = false)
    private String notificationTitle;

    @Column(nullable = false)
    private String notificationContent;

    @Column(nullable = false)
    private String relatedUrl;

    @Column(name = "is_read", nullable = false)
    private boolean read = false;

    @Column(nullable = false)
    private LocalDateTime notificationCreatedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_id", nullable = false)
    private Student student;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "study_id", nullable = false)
    private Study study;

    @Builder
    public Notification(NotificationCategory notificationCategory, Long entityId, String fileKey, String notificationTitle, String notificationContent, String relatedUrl, LocalDateTime notificationCreatedAt, Student student, Study study) {
        this.notificationCategory = notificationCategory;
        this.entityId = entityId;
        this.fileKey = fileKey;
        this.notificationTitle = notificationTitle;
        this.notificationContent = notificationContent;
        this.relatedUrl = relatedUrl;
        this.notificationCreatedAt = notificationCreatedAt;
        this.student = student;
        this.study = study;
    }

    public void markAsRead() {
        this.read = true;
    }
}
