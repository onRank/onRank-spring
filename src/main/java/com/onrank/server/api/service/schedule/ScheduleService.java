package com.onrank.server.api.service.schedule;

import com.onrank.server.api.service.attendance.AttendanceService;
import com.onrank.server.domain.schedule.Schedule;
import com.onrank.server.domain.schedule.ScheduleJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ScheduleService {

    private final ScheduleJpaRepository scheduleRepository;
    private final AttendanceService attendanceService;

     // 새로운 일정 등록
    @Transactional
    public void createSchedule(Schedule schedule) {
        Schedule savedSchedule = scheduleRepository.save(schedule);

        // 일정이 저장된 후 출석 정보 자동 생성
        attendanceService.createAttendancesForSchedule(savedSchedule);
    }
}
