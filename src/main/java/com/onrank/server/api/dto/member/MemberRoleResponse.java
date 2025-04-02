package com.onrank.server.api.dto.member;

import com.onrank.server.domain.member.Member;
import lombok.Getter;

@Getter
public class MemberRoleResponse {

    private String studyName;
    private String memberRole;

    public MemberRoleResponse(Member member) {
        this.studyName = member.getStudy().getStudyName();
        this.memberRole = getMemberRole();
    }
}
