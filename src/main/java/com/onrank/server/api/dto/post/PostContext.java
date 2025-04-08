package com.onrank.server.api.dto.post;

import com.onrank.server.api.dto.member.MemberRoleResponse;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class PostContext<T> {
    private MemberRoleResponse memberContext;
    private T data;
}
