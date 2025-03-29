package com.onrank.server.api.dto.member;

import com.onrank.server.domain.member.Member;
import lombok.Getter;

@Getter
public class MemberListResponse {

    private Long memberId;
    private String studentName;
    private String studentEmail;
    private String memberRole;

    public MemberListResponse(Member member) {
        this.memberId = member.getMemberId();
        this.studentName = member.getStudent().getStudentName();
        this.studentEmail = member.getStudent().getStudentEmail();
        this.memberRole = String.valueOf(member.getMemberRole());
    }
}