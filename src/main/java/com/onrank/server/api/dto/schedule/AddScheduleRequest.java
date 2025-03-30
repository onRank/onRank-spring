package com.onrank.server.api.dto.schedule;

import com.onrank.server.domain.schedule.Schedule;
import com.onrank.server.domain.study.Study;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class AddScheduleRequest {
    private String scheduleTitle;
    private String scheduleContent;
    private LocalDateTime scheduleStartingAt;

    public Schedule toEntity(Study study) {
        return Schedule.builder()
                .study(study)
                .scheduleTitle(scheduleTitle)
                .scheduleContent(scheduleContent)
                .scheduleStartingAt(scheduleStartingAt)
                .build();
    }
}
