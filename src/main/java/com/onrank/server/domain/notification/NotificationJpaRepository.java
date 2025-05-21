package com.onrank.server.domain.notification;

import com.onrank.server.domain.student.Student;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface NotificationJpaRepository extends JpaRepository<Notification, Long> {

    List<Notification> findByStudentOrderByNotificationCreatedAtDesc(Student student);

    List<Notification> findByNotificationCategoryAndEntityId(NotificationCategory category, Long entityId);

    List<Notification> findByStudentStudentIdAndStudyStudyId(Long studentId, Long studyId);

    List<Notification> findByStudentStudentId(Long studentId);
}
