package com.onrank.server.api.dto.attendance;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class AttendancePointRequest {

    private Integer presentPoint;
    private Integer absentPoint;
    private Integer latePoint;
}
