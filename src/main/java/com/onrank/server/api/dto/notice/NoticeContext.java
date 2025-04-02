package com.onrank.server.api.dto.notice;

import com.onrank.server.api.dto.member.MemberRoleResponse;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class NoticeContext<T> {
    private MemberRoleResponse memberContext;
    private T data;
}

