package com.onrank.server.api.controller.notification;

import com.onrank.server.api.dto.notification.NotificationResponse;
import com.onrank.server.api.dto.auth.CustomOAuth2User;
import com.onrank.server.api.service.notification.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/notifications")
public class NotificationController implements NotificationControllerDocs {

    private final NotificationService notificationService;

    // 알림 전체 조회 (본인 알림만, 최신순)
    @GetMapping
    public ResponseEntity<List<NotificationResponse>> getMyNotifications(@AuthenticationPrincipal CustomOAuth2User oAuth2User) {
        return ResponseEntity.ok(notificationService.getNotifications(oAuth2User.getName()));
    }

    // 알림 읽음 처리
    @PatchMapping("/{notificationId}/read")
    public ResponseEntity<Void> markAsRead(
            @PathVariable Long notificationId,
            @AuthenticationPrincipal CustomOAuth2User oAuth2User){

        notificationService.markAsRead(oAuth2User.getName(), notificationId);
        return ResponseEntity.ok().build();
    }
}
