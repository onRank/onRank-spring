package com.onrank.server.api.dto.schedule;

import com.onrank.server.domain.schedule.Schedule;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class ScheduleResponse {
    private Long scheduleId;
    private String scheduleTitle;
    private String scheduleContent;
    private LocalDateTime scheduleStartingAt;

    public ScheduleResponse(Schedule schedule) {
        this.scheduleId = schedule.getScheduleId();
        this.scheduleTitle = schedule.getScheduleTitle();
        this.scheduleContent = schedule.getScheduleContent();
        this.scheduleStartingAt = schedule.getScheduleStartingAt();
    }
}