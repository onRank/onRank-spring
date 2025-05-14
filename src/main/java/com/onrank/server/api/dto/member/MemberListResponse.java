package com.onrank.server.api.dto.member;

import com.onrank.server.domain.member.Member;
import com.onrank.server.domain.member.MemberRole;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "(스터디 관리)멤버 목록용 응답 DTO")
public record MemberListResponse (
        @Schema(description = "멤버 ID", example = "12")
        Long memberId,

        @Schema(description = "학생 이름", example = "김휘래")
        String studentName,

        @Schema(description = "학생 이메일", example = "hwirae@example.com")
        String studentEmail,

        @Schema(description = "학생 전화번호", example = "010-1234-5678")
        String studentPhoneNumber,

        @Schema(description = "학생 학교", example = "아주대학교")
        String studentSchool,

        @Schema(description = "학생 학과", example = "소프트웨어학과")
        String studentDepartment,

        @Schema(description = "스터디 내 역할", example = "PARTICIPANT")
        MemberRole memberRole
) {
    public static MemberListResponse from(Member member) {
        return new MemberListResponse(
                member.getMemberId(),
                member.getStudent().getStudentName(),
                member.getStudent().getStudentEmail(),
                member.getStudent().getStudentPhoneNumber(),
                member.getStudent().getStudentSchool(),
                member.getStudent().getStudentDepartment(),
                member.getMemberRole()
        );
    }
}
