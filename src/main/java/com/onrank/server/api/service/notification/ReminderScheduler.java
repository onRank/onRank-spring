package com.onrank.server.api.service.notification;

import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ReminderScheduler {

    private final NotificationService notificationService;

    // 오전 9시
    @Scheduled(cron = "0 0 9 * * *", zone = "Asia/Seoul")
    public void remindAt09() {
        notificationService.createRemindersForToday();
    }
}
