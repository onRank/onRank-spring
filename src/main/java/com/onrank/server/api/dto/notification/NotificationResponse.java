package com.onrank.server.api.dto.notification;

import com.onrank.server.domain.notification.Notification;
import com.onrank.server.domain.notification.NotificationCategory;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;

@Schema(description = "웹 알림 응답 DTO")
public record NotificationResponse(

        @Schema(description = "알림 ID", example = "1")
        Long notificationId,

        @Schema(description = "알림 카테고리", example = "NOTICE")
        NotificationCategory notificationCategory,

        @Schema(description = "스터디 이름", example = "운영체제 스터디")
        String studyName,

        @Schema(description = "스터디 이미지 URL", example = "https://onrank-bucket.s3.ap-northeast-2.amazonaws.com/studies/1/main.jpg")
        String studyImageUrl,

        @Schema(description = "알림 제목", example = "4월 1주차 공지사항")
        String notificationTitle,

        @Schema(description = "알림 메시지", example = "다음 주 스터디는 금요일입니다.")
        String notificationMessage,

        @Schema(description = "관련 URL", example = "/studies/1/notices/5")
        String relatedUrl,

        @Schema(description = "읽음 여부", example = "false")
        boolean read,

        @Schema(description = "생성 시각", example = "2025-05-03T00:15:30")
        LocalDateTime notificationCreatedAt
) {
    public static NotificationResponse from(Notification notification, String bucketName, String fileKey) {
        return new NotificationResponse(
                notification.getNotificationId(),
                notification.getNotificationCategory(),
                notification.getStudyName(),
                "https://" + bucketName + ".s3.ap-northeast-2.amazonaws.com/" + fileKey,
                notification.getNotificationTitle(),
                notification.getNotificationMessage(),
                notification.getRelatedUrl(),
                notification.isRead(),
                notification.getNotificationCreatedAt()
        );
    }
}
