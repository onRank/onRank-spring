package com.onrank.server.api.dto.member;

import com.onrank.server.api.dto.file.FileMetadataDto;
import com.onrank.server.domain.member.Member;
import com.onrank.server.domain.member.MemberRole;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@AllArgsConstructor
@RequiredArgsConstructor
public class MemberRoleResponse {

    private String studyName;
    private MemberRole memberRole;
    private FileMetadataDto file;

    public MemberRoleResponse(Member member, FileMetadataDto file) {
        this.studyName = member.getStudy().getStudyName();
        this.memberRole = member.getMemberRole();
        this.file = file;
    }
}
