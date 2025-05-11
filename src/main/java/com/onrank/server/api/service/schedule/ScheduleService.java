package com.onrank.server.api.service.schedule;

import com.onrank.server.api.dto.schedule.AddScheduleRequest;
import com.onrank.server.api.dto.schedule.ScheduleResponse;
import com.onrank.server.api.service.attendance.AttendanceService;
import com.onrank.server.api.service.member.MemberService;
import com.onrank.server.api.service.notification.NotificationService;
import com.onrank.server.api.service.study.StudyService;
import com.onrank.server.common.exception.CustomException;
import com.onrank.server.domain.member.Member;
import com.onrank.server.domain.notification.NotificationCategory;
import com.onrank.server.domain.schedule.Schedule;
import com.onrank.server.domain.schedule.ScheduleJpaRepository;
import com.onrank.server.domain.study.Study;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

import static com.onrank.server.common.exception.CustomErrorInfo.*;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ScheduleService {

    private final ScheduleJpaRepository scheduleRepository;
    private final AttendanceService attendanceService;
    private final StudyService studyService;
    private final NotificationService notificationService;
    private final MemberService memberService;

    public List<ScheduleResponse> getScheduleResponsesByStudyId(Long studyId) {
        return scheduleRepository.findAllByStudyStudyId(studyId).stream()
                .map(ScheduleResponse::new)
                .collect(Collectors.toList());
    }

    @Transactional
    public void createSchedule(String username, Long studyId, AddScheduleRequest request) {

        // CREATOR, HOST 만 가능
        if (!memberService.isMemberCreatorOrHost(username, studyId)) {
            throw new CustomException(ACCESS_DENIED);
        }

        Study study = studyService.findByStudyId(studyId)
                .orElseThrow(() -> new CustomException(STUDY_NOT_FOUND));
        Member member = memberService.findMemberByUsernameAndStudyId(username, studyId)
                .orElseThrow(() -> new CustomException(MEMBER_NOT_FOUND));

        // 일정 생성
        Schedule schedule = request.toEntity(study);
        scheduleRepository.save(schedule);

        // 출석 정보 자동 생성
        attendanceService.createAttendancesForSchedule(schedule);

        // 알림 생성
        notificationService.createNotification(NotificationCategory.SCHEDULE, schedule.getScheduleId(), studyId, schedule.getScheduleTitle(), schedule.getScheduleContent(),
                "/studies/" + studyId + "/schedules/" + schedule.getScheduleId(), member.getStudent());
    }

    @Transactional
    public void updateSchedule(Long scheduleId, AddScheduleRequest request) {
        Schedule schedule = scheduleRepository.findByScheduleId(scheduleId)
                .orElseThrow(() -> new IllegalArgumentException("Schedule not found"));
        schedule.update(request.getScheduleTitle(), request.getScheduleContent(), request.getScheduleStartingAt());
    }

    @Transactional
    public void deleteSchedule(Long scheduleId, Long studyId) {
        Schedule schedule = scheduleRepository.findByScheduleId(scheduleId)
                .orElseThrow(() -> new IllegalArgumentException("Schedule not found"));

        if (!schedule.getStudy().getStudyId().equals(studyId)) {
            throw new IllegalArgumentException("Schedule does not belong to this study");
        }
        scheduleRepository.delete(schedule);
    }
}
