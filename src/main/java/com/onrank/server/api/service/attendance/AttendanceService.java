package com.onrank.server.api.service.attendance;

import com.onrank.server.api.dto.attendance.AttendanceResponse;
import com.onrank.server.api.dto.notice.NoticeResponse;
import com.onrank.server.domain.attendance.Attendance;
import com.onrank.server.domain.attendance.AttendanceJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AttendanceService {

    private final AttendanceJpaRepository attendanceRepository;

    // 출석 조회를 위한 List<AttendanceResponse> 객체 생성
    public List<AttendanceResponse> getAttendanceResponsesByStudyId(Long studyId) {
        return attendanceRepository.findAllByStudyId(studyId)
                .stream()
                .map(AttendanceResponse::new)
                .collect(Collectors.toList());
    }
}
