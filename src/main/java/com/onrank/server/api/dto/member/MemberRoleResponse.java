package com.onrank.server.api.dto.member;

import com.onrank.server.domain.member.Member;
import com.onrank.server.domain.member.MemberRole;
import lombok.Getter;

@Getter
public class MemberRoleResponse {

    private String studyName;
    private MemberRole memberRole;

    public MemberRoleResponse(Member member) {
        this.studyName = member.getStudy().getStudyName();
        this.memberRole = member.getMemberRole();
    }
}
