package com.onrank.server.api.service.notification;

import com.onrank.server.common.exception.CustomException;
import com.onrank.server.domain.assignment.AssignmentJpaRepository;
import com.onrank.server.domain.notification.Notification;
import com.onrank.server.domain.schedule.ScheduleJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

import static com.onrank.server.common.exception.CustomErrorInfo.ASSIGNMENT_NOT_FOUND;
import static com.onrank.server.common.exception.CustomErrorInfo.SCHEDULE_NOT_FOUND;

@Component
@RequiredArgsConstructor
public class NotificationTimeResolver {

    private final ScheduleJpaRepository scheduleRepository;
    private final AssignmentJpaRepository assignmentRepository;

    // 캘린더를 위한 시간 추출
    public LocalDateTime resolveTime(Notification notification) {
        return switch (notification.getNotificationCategory()) {
            case SCHEDULE -> scheduleRepository.findByScheduleId(notification.getEntityId())
                    .orElseThrow(() -> new CustomException(SCHEDULE_NOT_FOUND))
                    .getScheduleStartingAt();

            case ASSIGNMENT -> assignmentRepository.findByAssignmentId(notification.getEntityId())
                    .orElseThrow(() -> new CustomException(ASSIGNMENT_NOT_FOUND))
                    .getAssignmentDueDate();

            default -> notification.getNotificationCreatedAt();
        };
    }
}
