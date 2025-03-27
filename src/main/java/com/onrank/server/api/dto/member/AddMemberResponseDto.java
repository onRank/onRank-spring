package com.onrank.server.api.dto.member;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class AddMemberResponseDto {

    private Long memberId;
    private String message;
}