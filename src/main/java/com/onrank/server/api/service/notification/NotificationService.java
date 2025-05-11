package com.onrank.server.api.service.notification;

import com.onrank.server.api.dto.notification.NotificationResponse;
import com.onrank.server.api.service.file.FileService;
import com.onrank.server.api.service.student.StudentService;
import com.onrank.server.api.service.study.StudyService;
import com.onrank.server.common.exception.CustomException;
import com.onrank.server.domain.assignment.Assignment;
import com.onrank.server.domain.assignment.AssignmentJpaRepository;
import com.onrank.server.domain.member.Member;
import com.onrank.server.domain.notification.Notification;
import com.onrank.server.domain.notification.NotificationCategory;
import com.onrank.server.domain.notification.NotificationJpaRepository;
import com.onrank.server.domain.schedule.Schedule;
import com.onrank.server.domain.schedule.ScheduleJpaRepository;
import com.onrank.server.domain.student.Student;
import com.onrank.server.domain.study.Study;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import static com.onrank.server.common.exception.CustomErrorInfo.*;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class NotificationService {

    private final NotificationJpaRepository notificationRepository;
    private final StudentService studentService;
    private final StudyService studyService;
    private final ScheduleJpaRepository scheduleJpaRepository;
    private final AssignmentJpaRepository assignmentRepository;
    private final FileService fileService;

    // 알림 생성
    @Transactional
    public void createNotification(NotificationCategory category, Long entityId, Long studyId, String title, String content, String relatedUrl, Student student) {

        Study study = studyService.findByStudyId(studyId)
                .orElseThrow(() -> new CustomException(STUDY_NOT_FOUND));

        String fileKey = fileService.getStudyImageFileKeyByStudyId(studyId);

        Notification notification = Notification.builder()
                .notificationCategory(category)
                .entityId(entityId)
                .studyName(study.getStudyName())
                .fileKey(fileKey)
                .notificationTitle(title)
                .notificationContent(content)
                .relatedUrl(relatedUrl)
                .notificationCreatedAt(LocalDateTime.now())
                .student(student)
                .build();
        notificationRepository.save(notification);
    }

    // 특정 학생의 알림 조회 (최신순)
    public List<NotificationResponse> getNotifications(String username) {

        Student student = studentService.findByUsername(username)
                .orElseThrow(() -> new CustomException(STUDENT_NOT_FOUND));

        return notificationRepository.findByStudentOrderByNotificationCreatedAtDesc(student).stream()
                .map(notification ->
                        NotificationResponse.from(notification, "onrank-bucket", notification.getFileKey()))
                .collect(Collectors.toList());
    }

    // 알림 읽음 처리
    @Transactional
    public void markAsRead(Long notificationId) {
        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new CustomException(NOTIFICATION_NOT_FOUND));
        notification.markAsRead();
    }

    @Transactional
    public void deleteNotification(NotificationCategory category, Long entityId) {

        Notification notification = notificationRepository.findByNotificationCategoryAndEntityId(category, entityId)
                .orElseThrow(() -> new CustomException(NOTIFICATION_NOT_FOUND));
        notificationRepository.delete(notification);
    }

    @Transactional
    public void createRemindersForToday() {

        LocalDate today = LocalDate.now();

        // 1. 오늘 일정
        List<Schedule> schedules = scheduleJpaRepository.findByScheduleStartingAtBetween(
                today.atStartOfDay(),
                today.atTime(23, 59, 59)
        );

        for (Schedule schedule : schedules) {
            Study study = schedule.getStudy();
            for (Member member : study.getMembers()) {
                createNotification(
                        NotificationCategory.SCHEDULE,
                        schedule.getScheduleId(),
                        study.getStudyId(),
                        "[오늘 일정] " + schedule.getScheduleTitle(),
                        schedule.getScheduleContent(),
                        "/studies/" + study.getStudyId() + "/schedules/" + schedule.getScheduleId(),
                        member.getStudent()
                );
            }
        }

        // 2. 오늘 마감 과제
        List<Assignment> assignments = assignmentRepository.findByAssignmentDueDateBetween(
                today.atStartOfDay(),
                today.atTime(23, 59, 59)
        );

        for(Assignment assignment : assignments) {
            Study study = assignment.getStudy();
            for (Member member : study.getMembers()) {
                createNotification(
                        NotificationCategory.ASSIGNMENT,
                        assignment.getAssignmentId(),
                        study.getStudyId(),
                        "[오늘 마감] " + assignment.getAssignmentTitle(),
                        assignment.getAssignmentContent(),
                        "/studies/" + study.getStudyId() + "/assignments/" + assignment.getAssignmentId(),
                        member.getStudent()
                );
            }
        }
    }
}
