package com.onrank.server.api.dto.member;

import com.onrank.server.domain.member.Member;
import lombok.Getter;

@Getter
public class MemberRoleDto {
    private String memberRole;
    private String studyName;

    public MemberRoleDto(Member member) {
        this.memberRole = String.valueOf(member.getMemberRole());
        this.studyName = member.getStudy().getStudyName();
    }
}
