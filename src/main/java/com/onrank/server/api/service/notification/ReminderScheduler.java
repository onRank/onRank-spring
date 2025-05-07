package com.onrank.server.api.service.notification;

import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ReminderScheduler {

    private final NotificationService notificationService;

    //매일 오전 9시 실행
    @Scheduled(cron = "0 0 9 * * *")
    public void remindTodaySchedulesAndAssingments() {
        notificationService.createRemindersForToday();
    }

}
