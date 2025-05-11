package com.onrank.server.domain.notification;

import com.onrank.server.domain.student.Student;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface NotificationJpaRepository extends JpaRepository<Notification, Long> {

    List<Notification> findByStudentOrderByNotificationCreatedAtDesc(Student student);

    Optional<Notification> findByNotificationCategoryAndEntityId(NotificationCategory category, Long entityId);

}
