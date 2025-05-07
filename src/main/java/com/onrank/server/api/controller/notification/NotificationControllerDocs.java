package com.onrank.server.api.controller.notification;

import com.onrank.server.api.dto.notification.NotificationResponse;
import com.onrank.server.api.dto.oauth.CustomOAuth2User;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

public interface NotificationControllerDocs {

    @Operation(summary = "내 알림 목록 조회", description = "로그인한 사용자의 알림을 최신순으로 조회합니다.")
    ResponseEntity<List<NotificationResponse>> getMyNotifications(
            @Parameter(description = "로그인한 사용자 정보", hidden = true) CustomOAuth2User oAuth2User
    );

    @Operation(summary = "알림 읽음 처리", description = "지정된 알림 ID의 알림을 읽음 처리합니다.")
    ResponseEntity<Void> markAsRead(
            @Parameter(description = "읽음 처리할 알림 ID", example = "1") @PathVariable Long notificationId
    );
}
