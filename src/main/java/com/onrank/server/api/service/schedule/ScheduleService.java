package com.onrank.server.api.service.schedule;

import com.onrank.server.api.dto.schedule.AddScheduleRequest;
import com.onrank.server.api.dto.schedule.ScheduleResponse;
import com.onrank.server.api.service.attendance.AttendanceService;
import com.onrank.server.api.service.study.StudyService;
import com.onrank.server.domain.schedule.Schedule;
import com.onrank.server.domain.schedule.ScheduleJpaRepository;
import com.onrank.server.domain.study.Study;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ScheduleService {

    private final ScheduleJpaRepository scheduleRepository;
    private final AttendanceService attendanceService;
    private final StudyService studyService;

    public List<ScheduleResponse> getScheduleResponsesByStudyId(Long studyId) {
        return scheduleRepository.findAllByStudyStudyId(studyId).stream()
                .map(ScheduleResponse::new)
                .collect(Collectors.toList());
    }

    public ScheduleResponse getScheduleResponse(Long scheduleId) {
        Schedule schedule = scheduleRepository.findByScheduleId(scheduleId)
                .orElseThrow(() -> new IllegalArgumentException("Schedule not found"));
        return new ScheduleResponse(schedule);
    }

    @Transactional
    public void createSchedule(Long studyId, AddScheduleRequest request) {
        Study study = studyService.findByStudyId(studyId)
                .orElseThrow(() -> new IllegalArgumentException("Study not found"));

        // 일정 생성
        Schedule schedule = request.toEntity(study);
        scheduleRepository.save(schedule);

//        // 출석 정보 자동 생성
//        attendanceService.createAttendancesForSchedule(schedule);
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
