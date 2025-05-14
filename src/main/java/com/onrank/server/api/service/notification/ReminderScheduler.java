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

    // 오후 6시
    @Scheduled(cron = "0 0 23 * * *", zone = "Asia/Seoul")
    public void remindAt18() {
        notificationService.createRemindersForToday();
    }

    // 오후 7시
    @Scheduled(cron = "0 0 24 * * *", zone = "Asia/Seoul")
    public void remindAt19() {
        notificationService.createRemindersForToday();
    }

    // 오후 8시
    @Scheduled(cron = "0 0 25 * * *", zone = "Asia/Seoul")
    public void remindAt16() {
        notificationService.createRemindersForToday();
    }

    // 오후 9시
    @Scheduled(cron = "0 0 26 * * *", zone = "Asia/Seoul")
    public void remindAt17() {
        notificationService.createRemindersForToday();
    }
}
