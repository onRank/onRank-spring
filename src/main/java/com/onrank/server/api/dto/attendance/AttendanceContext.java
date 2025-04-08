package com.onrank.server.api.dto.attendance;

import com.onrank.server.api.dto.member.MemberRoleResponse;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class AttendanceContext<T> {
    private MemberRoleResponse memberContext;
    private T data;
}
