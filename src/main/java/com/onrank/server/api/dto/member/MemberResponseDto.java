package com.onrank.server.api.dto.member;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class MemberResponseDto {

    private String studentName;
    private String studentEmail;
}