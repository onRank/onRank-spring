package com.onrank.server.api.controller.member;

import com.onrank.server.api.dto.common.ContextResponse;
import com.onrank.server.api.dto.common.MemberStudyContext;
import com.onrank.server.api.dto.member.AddMemberRequest;
import com.onrank.server.api.dto.member.MemberManagementResponse;
import com.onrank.server.api.dto.member.MemberRoleRequest;
import com.onrank.server.api.dto.auth.CustomOAuth2User;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

public interface MemberControllerDocs {

    @Operation(summary = "스터디 멤버 목록 조회", description = "스터디에 소속된 멤버 목록을 조회합니다. HOST 또는 CREATOR 권한이 필요합니다.")
    ResponseEntity<ContextResponse<MemberManagementResponse>> getMembers(
            @Parameter(description = "스터디 ID", example = "1") @PathVariable Long studyId,
            @Parameter(hidden = true) CustomOAuth2User oAuth2User
    );

    @Operation(summary = "스터디 멤버 추가", description = "학생 이메일을 통해 스터디에 새로운 멤버를 추가합니다.")
    ResponseEntity<MemberStudyContext> addMember(
            @Parameter(description = "스터디 ID", example = "1") @PathVariable Long studyId,
            @RequestBody AddMemberRequest addMemberRequest,
            @Parameter(hidden = true) CustomOAuth2User oAuth2User
    );

    @Operation(summary = "스터디 멤버 역할 변경", description = "스터디 내 특정 멤버의 역할을 변경합니다.")
    ResponseEntity<MemberStudyContext> updateMemberRole(
            @Parameter(description = "스터디 ID", example = "1") @PathVariable Long studyId,
            @Parameter(description = "멤버 ID", example = "15") @PathVariable Long memberId,
            @RequestBody MemberRoleRequest memberRoleRequest,
            @Parameter(hidden = true) CustomOAuth2User oAuth2User
    );

    @Operation(summary = "스터디 멤버 삭제", description = "스터디 내 특정 멤버를 삭제합니다. 자기 자신은 삭제할 수 없습니다.")
    ResponseEntity<MemberStudyContext> deleteMember(
            @Parameter(description = "스터디 ID", example = "1") @PathVariable Long studyId,
            @Parameter(description = "멤버 ID", example = "15") @PathVariable Long memberId,
            @Parameter(hidden = true) CustomOAuth2User oAuth2User
    );
}
