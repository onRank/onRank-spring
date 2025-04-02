package com.onrank.server.api.dto.member;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
@AllArgsConstructor
public class MemberListResponse {

    private String StudyName;
    private List<MemberResponse> members;
}