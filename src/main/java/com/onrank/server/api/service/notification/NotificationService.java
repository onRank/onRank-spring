package com.onrank.server.api.service.notification;

import com.onrank.server.api.dto.notification.NotificationResponse;
import com.onrank.server.api.service.student.StudentService;
import com.onrank.server.api.service.study.StudyService;
import com.onrank.server.common.exception.CustomException;
import com.onrank.server.domain.file.FileCategory;
import com.onrank.server.domain.file.FileMetadata;
import com.onrank.server.domain.file.FileMetadataJpaRepository;
import com.onrank.server.domain.notification.Notification;
import com.onrank.server.domain.notification.NotificationCategory;
import com.onrank.server.domain.notification.NotificationJpaRepository;
import com.onrank.server.domain.student.Student;
import com.onrank.server.domain.study.Study;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
    private final FileMetadataJpaRepository fileMetadataRepository;

    // 알림 생성
    @Transactional
    public Notification createNotification(NotificationCategory category, Long studyId, String title, String message, String relatedUrl, Student student) {

        Study study = studyService.findByStudyId(studyId)
                .orElseThrow(() -> new CustomException(STUDY_NOT_FOUND));

        String fileKey = null;
        List<FileMetadata> files = fileMetadataRepository.findByCategoryAndEntityId(FileCategory.STUDY, studyId);
        if (!files.isEmpty()) {
            FileMetadata file = files.get(0);
            fileKey = file.getFileKey();
        }

        Notification notification = Notification.builder()
                .notificationCategory(category)
                .studyName(study.getStudyName())
                .fileKey(fileKey)
                .notificationTitle(title)
                .notificationMessage(message)
                .relatedUrl(relatedUrl)
                .notificationCreatedAt(LocalDateTime.now())
                .student(student)
                .build();
        return notificationRepository.save(notification);
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
}
