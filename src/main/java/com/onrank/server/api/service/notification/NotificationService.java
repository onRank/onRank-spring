package com.onrank.server.api.service.notification;

import com.onrank.server.api.dto.notification.NotificationResponse;
import com.onrank.server.api.service.file.FileService;
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
import com.onrank.server.domain.student.StudentJpaRepository;
import com.onrank.server.domain.study.Study;
import com.onrank.server.domain.study.StudyJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
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
    private final StudentJpaRepository studentRepository;
    private final StudyJpaRepository studyRepository;
    private final ScheduleJpaRepository scheduleRepository;
    private final AssignmentJpaRepository assignmentRepository;
    private final FileService fileService;

    @Value("${cloud.aws.s3.bucket}")
    private String bucketName;

    // 알림 생성
    @Transactional
    public void createNotification(NotificationCategory category, Long entityId, Long studyId, String title, String content, String relatedUrl) {

        Study study = studyRepository.findByStudyId(studyId)
                .orElseThrow(() -> new CustomException(STUDY_NOT_FOUND));

        String fileKey = fileService.getStudyImageFileKeyByStudyId(studyId);
        LocalDateTime now = LocalDateTime.now();
        for (Member member : study.getMembers()) {
            Notification notification = Notification.builder()
                    .notificationCategory(category)
                    .entityId(entityId)
                    .fileKey(fileKey)
                    .notificationTitle(title)
                    .notificationContent(content)
                    .relatedUrl(relatedUrl)
                    .notificationCreatedAt(now)
                    .student(member.getStudent())
                    .study(study)
                    .build();
            notificationRepository.save(notification);
        }
    }

    @Transactional
    public void createNotificationForNewMember(Long studyId, Long studentId){
        Study study = studyRepository.findByStudyId(studyId)
                .orElseThrow(() -> new CustomException(STUDY_NOT_FOUND));
        Student student = studentRepository.findByStudentId(studentId)
                .orElseThrow(() -> new CustomException(STUDENT_NOT_FOUND));

        String fileKey = fileService.getStudyImageFileKeyByStudyId(studyId);
        LocalDateTime now = LocalDateTime.now();

        Notification notification = Notification.builder()
                .notificationCategory(NotificationCategory.STUDY)
                .entityId(studyId)
                .fileKey(fileKey)
                .notificationTitle(study.getStudyName() + " 스터디 가입!")
                .notificationContent(study.getStudyName() + " 스터디에 멤버로 추가되었습니다!")
                .relatedUrl("/studies/" + studyId)
                .notificationCreatedAt(now)
                .student(student)
                .study(study)
                .build();

            notificationRepository.save(notification);
    }


    // 특정 학생의 알림 조회 (최신순)
    public List<NotificationResponse> getNotifications(String username) {

        Student student = studentRepository.findByUsername(username)
                .orElseThrow(() -> new CustomException(STUDENT_NOT_FOUND));

        return notificationRepository.findByStudentOrderByNotificationCreatedAtDesc(student).stream()
                .map(notification ->
                        NotificationResponse.from(notification, bucketName, notification.getFileKey()))
                .collect(Collectors.toList());
    }

    // 알림 읽음 처리
    @Transactional
    public void markAsRead(String username, Long notificationId) {
        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new CustomException(NOTIFICATION_NOT_FOUND));

        // 본인만 가능
        if(!notification.getStudent().getUsername().equals(username)) {
            throw new CustomException(ACCESS_DENIED);
        }
        notification.markAsRead();
    }

    @Transactional
    public void deleteNotificationByEntity(NotificationCategory category, Long entityId) {

        List<Notification> notifications = notificationRepository.findByNotificationCategoryAndEntityId(category, entityId);
        notificationRepository.deleteAll(notifications);
    }

    @Transactional
    public void deleteNotificationByMember(Long studentId, Long studyId) {

        List<Notification> notifications = notificationRepository.findByStudentStudentIdAndStudyStudyId(studentId, studyId);
        notificationRepository.deleteAll(notifications);
    }

    @Transactional
    public void createRemindersForToday() {

        LocalDate today = LocalDate.now();

        // 1. 오늘 일정
        List<Schedule> schedules = scheduleRepository.findByScheduleStartingAtBetween(
                today.atStartOfDay(),
                today.atTime(23, 59, 59)
        );

        for (Schedule schedule : schedules) {
            Study study = schedule.getStudy();
            createNotification(
                    NotificationCategory.REMINDER_SCHEDULE,
                    schedule.getScheduleId(),
                    study.getStudyId(),
                    schedule.getScheduleTitle(),
                    schedule.getScheduleContent(),
                    "/studies/" + study.getStudyId() + "/schedules"
            );
        }

        // 2. 오늘 마감 과제
        List<Assignment> assignments = assignmentRepository.findByAssignmentDueDateBetween(
                today.atStartOfDay(),
                today.atTime(23, 59, 59)
        );

        for(Assignment assignment : assignments) {
            Study study = assignment.getStudy();
            createNotification(
                    NotificationCategory.REMINDER_ASSIGNMENT,
                    assignment.getAssignmentId(),
                    study.getStudyId(), assignment.getAssignmentTitle(),
                    assignment.getAssignmentContent(),
                    "/studies/" + study.getStudyId() + "/assignments/" + assignment.getAssignmentId()
            );
        }
    }

    // JPQL 사용
    public List<Notification> getScheduleAndAssignmentNotifications(Student student) {
        return notificationRepository.findByStudentAndCategories(
                student.getStudentId(),
                List.of(NotificationCategory.SCHEDULE, NotificationCategory.ASSIGNMENT)
        );
    }
}
