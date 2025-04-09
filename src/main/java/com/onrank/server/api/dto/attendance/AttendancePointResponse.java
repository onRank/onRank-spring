package com.onrank.server.api.dto.attendance;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class AttendancePointResponse {
    private int presentPoint;
    private int absentPoint;
    private int latePoint;
}
