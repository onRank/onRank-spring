package com.onrank.server.api.dto.common;

import com.onrank.server.api.dto.file.FileMetadataDto;
import com.onrank.server.domain.member.Member;
import com.onrank.server.domain.member.MemberRole;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@AllArgsConstructor
@RequiredArgsConstructor
@Schema(description = "사용자 기준 context 정보")
public class MemberStudyContext {

    @Schema(description = "스터디 이름", example = "알고리즘 마스터")
    private String studyName;

    @Schema(description = "멤버의 역할 (HOST, PARTICIPANT, CREATOR)", example = "HOST")
    private MemberRole memberRole;

    @Schema(description = "스터디 대표 이미지 정보")
    private FileMetadataDto file; // studyImage 추후 변경

    public MemberStudyContext(Member member, FileMetadataDto file) {
        this.studyName = member.getStudy().getStudyName();
        this.memberRole = member.getMemberRole();
        this.file = file;
    }
}
