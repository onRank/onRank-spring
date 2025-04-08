package com.onrank.server.api.dto.schedule;

import com.onrank.server.api.dto.member.MemberRoleResponse;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ScheduleContext<T> {
    private MemberRoleResponse memberContext;
    private T data;
}