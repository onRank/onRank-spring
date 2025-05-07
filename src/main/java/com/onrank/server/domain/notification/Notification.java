package com.onrank.server.domain.notification;

import com.onrank.server.domain.student.Student;
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
    private String studyName;

    private String fileKey;

    @Column(nullable = false)
    private String notificationTitle;

    @Column(nullable = false)
    private String notificationContent;

    @Column(nullable = false)
    private String relatedUrl;

    @Column(nullable = false)
    private boolean read = false;

    @Column(nullable = false)
    private LocalDateTime notificationCreatedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_id", nullable = false)
    private Student student;

    @Builder
    public Notification(NotificationCategory notificationCategory, String studyName, String fileKey, String notificationTitle, String notificationContent, String relatedUrl, LocalDateTime notificationCreatedAt, Student student) {
        this.notificationCategory = notificationCategory;
        this.studyName = studyName;
        this.fileKey = fileKey;
        this.notificationTitle = notificationTitle;
        this.notificationContent = notificationContent;
        this.relatedUrl = relatedUrl;
        this.notificationCreatedAt = notificationCreatedAt;
        this.student = student;
    }

    public void markAsRead() {
        this.read = true;
    }
}
