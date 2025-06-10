package com.onrank.server.api.calendar;

import com.onrank.server.api.dto.student.CalendarResponse;
import com.onrank.server.api.service.notification.NotificationService;
import com.onrank.server.common.exception.CustomException;
import com.onrank.server.domain.notification.Notification;
import com.onrank.server.domain.student.Student;
import com.onrank.server.domain.student.StudentJpaRepository;
import com.onrank.server.domain.study.Study;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.onrank.server.common.exception.CustomErrorInfo.STUDENT_NOT_FOUND;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CalendarService {

    private final StudentJpaRepository studentRepository;
    private final NotificationService notificationService;
    private final CalendarAssembler calendarAssembler;

    public List<CalendarResponse> getCalendar(String username) {
        Student student = studentRepository.findByUsername(username)
                .orElseThrow(() -> new CustomException(STUDENT_NOT_FOUND));

        List<Notification> notifications = notificationService.getScheduleAndAssignmentNotifications(student);

        Map<Study, List<Notification>> groupedByStudy = notifications.stream()
                .collect(Collectors.groupingBy(Notification::getStudy));

        return groupedByStudy.entrySet().stream()
                .map(entry -> calendarAssembler.assembleCalendarResponse(student, entry.getKey(), entry.getValue()))
                .toList();
    }
}
