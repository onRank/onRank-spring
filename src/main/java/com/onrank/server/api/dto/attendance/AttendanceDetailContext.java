package com.onrank.server.api.dto.attendance;

import com.onrank.server.api.dto.common.MemberStudyContext;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class AttendanceDetailContext<T> {
    private MemberStudyContext memberContext;
    private String scheduleTitle;
    private LocalDateTime scheduleStartingAt;

    private T data; // 출석자 목록 (List<AttendanceMemberResponse>)
}

