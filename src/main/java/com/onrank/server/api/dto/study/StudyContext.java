package com.onrank.server.api.dto.study;

import com.onrank.server.api.dto.member.MemberRoleResponse;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class StudyContext<T> {
    private MemberRoleResponse memberContext;
    private T data;
}