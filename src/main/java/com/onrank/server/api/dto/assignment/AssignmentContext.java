package com.onrank.server.api.dto.assignment;

import com.onrank.server.api.dto.member.MemberRoleResponse;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class AssignmentContext<T> {

    private MemberRoleResponse memberContext;
    private T data;
}
