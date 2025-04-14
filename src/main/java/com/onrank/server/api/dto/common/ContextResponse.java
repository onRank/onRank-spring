package com.onrank.server.api.dto.common;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ContextResponse <T> {
    private MemberStudyContext memberContext;
    private T data;
}