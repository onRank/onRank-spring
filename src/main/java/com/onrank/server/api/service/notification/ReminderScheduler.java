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

    @Scheduled(cron = "0 0 11 * * *", zone = "Asia/Seoul")
    public void remindAt11() {
        notificationService.createRemindersForToday();
    }

    @Scheduled(cron = "0 0 13 * * *", zone = "Asia/Seoul")
    public void remindAt13() {
        notificationService.createRemindersForToday();
    }

    @Scheduled(cron = "0 0 15 * * *", zone = "Asia/Seoul")
    public void remindAt15() {
        notificationService.createRemindersForToday();
    }

    @Scheduled(cron = "0 0 17 * * *", zone = "Asia/Seoul")
    public void remindAt17() {
        notificationService.createRemindersForToday();
    }

    @Scheduled(cron = "0 0 19 * * *", zone = "Asia/Seoul")
    public void remindAt19() {
        notificationService.createRemindersForToday();
    }

    @Scheduled(cron = "0 0 21 * * *", zone = "Asia/Seoul")
    public void remindAt21() {
        notificationService.createRemindersForToday();
    }

    @Scheduled(cron = "0 0 23 * * *", zone = "Asia/Seoul")
    public void remindAt01() {
        notificationService.createRemindersForToday();
    }

    @Scheduled(cron = "0 0 1 * * *", zone = "Asia/Seoul")
    public void remindAt16() {
        notificationService.createRemindersForToday();
    }
}
