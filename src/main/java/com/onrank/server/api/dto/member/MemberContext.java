package com.onrank.server.api.dto.member;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class MemberContext<T> {
    private MemberRoleResponse memberContext;
    private T data;
}

