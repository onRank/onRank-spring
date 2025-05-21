package com.onrank.server.api.service.student;

import com.onrank.server.api.dto.student.AddStudentRequest;
import com.onrank.server.api.dto.student.CalendarDetailResponse;
import com.onrank.server.api.dto.student.CalendarResponse;
import com.onrank.server.api.dto.student.StudentResponse;
import com.onrank.server.api.dto.study.MyPageStudyListResponse;
import com.onrank.server.api.service.study.StudyService;
import com.onrank.server.common.exception.CustomException;
import com.onrank.server.domain.assignment.AssignmentJpaRepository;
import com.onrank.server.domain.member.Member;
import com.onrank.server.domain.member.MemberJpaRepository;
import com.onrank.server.domain.notification.Notification;
import com.onrank.server.domain.notification.NotificationCategory;
import com.onrank.server.domain.notification.NotificationJpaRepository;
import com.onrank.server.domain.schedule.ScheduleJpaRepository;
import com.onrank.server.domain.student.Student;
import com.onrank.server.domain.student.StudentJpaRepository;
import com.onrank.server.domain.study.Study;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.onrank.server.common.exception.CustomErrorInfo.*;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class StudentService {

    private final StudentJpaRepository studentRepository;
    private final NotificationJpaRepository notificationRepository;
    private final ScheduleJpaRepository scheduleRepository;
    private final AssignmentJpaRepository assignmentRepository;
    private final StudyService studyService;
    private final MemberJpaRepository memberRepository;

    public boolean checkIfNewUser(String username) {
        return !studentRepository.existsByUsername(username);
    }

    public Optional<Student> findByUsername(String username) {
        return studentRepository.findByUsername(username);
    }

    public boolean checkIfExist(String studentEmil) {
        return studentRepository.existsByStudentEmail(studentEmil);
    }

    @Transactional
    public void createStudent(Student student) {
        studentRepository.save(student);
    }

    @Transactional
    public void updateStudent(String username, Long studentId, AddStudentRequest addStudentRequest) {

        // Student 본인만 가능
        Student student = studentRepository.findByStudentId(studentId)
                .orElseThrow(() -> new CustomException(STUDENT_NOT_FOUND));
        if (!student.getUsername().equals(username)) {
            throw new CustomException(ACCESS_DENIED);
        }

        student.update(
                addStudentRequest.getStudentName(),
                addStudentRequest.getStudentSchool(),
                addStudentRequest.getStudentDepartment(),
                addStudentRequest.getStudentPhoneNumber()
        );
    }

    // 마이페이지 조회
    public StudentResponse getMyPage(String username) {
        Student student = studentRepository.findByUsername(username)
                .orElseThrow(() -> new CustomException(STUDENT_NOT_FOUND));

        List<MyPageStudyListResponse> studyList = studyService.getMyPageStudyListResponsesByUsername(username);
        return StudentResponse.from(student, studyList);
    }

    public List<CalendarResponse> getCalendar(String username) {
        Student student = studentRepository.findByUsername(username)
                .orElseThrow(() ->  new CustomException(STUDENT_NOT_FOUND));

        // 해당 학생 알림 전체 조회
        List<Notification> notifications = notificationRepository.findByStudentStudentId(student.getStudentId());

        // ASSIGNMENT, SCHEDULE 만 필터링
        List<Notification> filtered = notifications.stream()
                .filter(n -> n.getNotificationCategory() == NotificationCategory.ASSIGNMENT
                        || n.getNotificationCategory() == NotificationCategory.SCHEDULE)
                .toList();

        // 스터디별로 그룹화
        Map<Study, List<Notification>> groupedByStudy = filtered.stream()
                .collect(Collectors.groupingBy(Notification::getStudy));

        // DTO 변환
        return groupedByStudy.entrySet().stream()
                .map(entry -> {
                    Study study = entry.getKey();

                    Member member = memberRepository.findByStudentStudentIdAndStudyStudyId(student.getStudentId(), study.getStudyId())
                            .orElseThrow(() -> new CustomException(MEMBER_NOT_FOUND));

                    List<CalendarDetailResponse> detailList = entry.getValue().stream()
                            .map(n -> {

                                LocalDateTime time;
                                if (n.getNotificationCategory() == NotificationCategory.SCHEDULE) {
                                    time = scheduleRepository.findByScheduleId(n.getEntityId())
                                            .orElseThrow(() -> new CustomException(SCHEDULE_NOT_FOUND))
                                            .getScheduleStartingAt();
                                } else if (n.getNotificationCategory() == NotificationCategory.ASSIGNMENT) {
                                    time = assignmentRepository.findByAssignmentId(n.getEntityId())
                                            .orElseThrow(() -> new CustomException(ASSIGNMENT_NOT_FOUND))
                                            .getAssignmentDueDate();
                                } else {
                                    time = n.getNotificationCreatedAt();
                                }
                                return CalendarDetailResponse.builder()
                                        .title(n.getNotificationTitle())
                                        .relatedUrl(n.getRelatedUrl())
                                        .category(n.getNotificationCategory())
                                        .time(time)
                                        .build();
                            })
                            .toList();

                    return new CalendarResponse(
                            study.getStudyName(),
                            member.getMemberColorCode(),
                            detailList
                    );
                })
                .toList();
    }
}