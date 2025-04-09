package com.onrank.server.api.controller.member;

import com.onrank.server.api.dto.member.*;
import com.onrank.server.api.dto.oauth.CustomOAuth2User;
import com.onrank.server.api.service.member.MemberService;
import com.onrank.server.api.service.student.StudentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/studies/{studyId}/management/members")
@RequiredArgsConstructor
public class MemberController {

    private final MemberService memberService;
    private final StudentService studentService;

    @GetMapping
    public ResponseEntity<MemberContext<MemberListResponse>> getMembers(
            @PathVariable Long studyId,
            @AuthenticationPrincipal CustomOAuth2User oAuth2User) {

        // CREATOR, HOST 만 가능
        if (!memberService.isMemberCreatorOrHost(oAuth2User.getName(), studyId)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        MemberRoleResponse context = memberService.getMyRoleInStudy(oAuth2User.getName(), studyId);
        MemberListResponse response = memberService.getMembersForStudy(studyId);
        return ResponseEntity.ok(new MemberContext<>(context, response));
    }

    @PostMapping("/add")
    public ResponseEntity<MemberRoleResponse> addMember(
            @PathVariable Long studyId,
            @RequestBody AddMemberRequest addMemberRequest,
            @AuthenticationPrincipal CustomOAuth2User oAuth2User) {

        // CREATOR, HOST 만 가능
        if (!memberService.isMemberCreatorOrHost(oAuth2User.getName(), studyId)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        // 서비스에 존재 여부
        if (!studentService.checkIfExist(addMemberRequest.getStudentEmail())) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        memberService.addMemberToStudy(studyId, addMemberRequest);
        MemberRoleResponse response = memberService.getMyRoleInStudy(oAuth2User.getName(), studyId);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PutMapping("/{memberId}/role")
    public ResponseEntity<MemberRoleResponse> updateMemberRole(
            @PathVariable Long studyId,
            @PathVariable Long memberId,
            @RequestBody MemberRoleRequest memberRoleRequest,
            @AuthenticationPrincipal CustomOAuth2User oAuth2User) {

        // CREATOR, HOST 만 가능
        if (!memberService.isMemberCreatorOrHost(oAuth2User.getName(), studyId)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        memberService.updateMemberRole(studyId, memberId, memberRoleRequest.getMemberRole());
        MemberRoleResponse response = memberService.getMyRoleInStudy(oAuth2User.getName(), studyId);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{memberId}")
    public ResponseEntity<MemberRoleResponse> deleteMember(
            @PathVariable Long studyId,
            @PathVariable Long memberId,
            @AuthenticationPrincipal CustomOAuth2User oAuth2User) {

        // CREATOR, HOST 만 가능
        if (!memberService.isMemberCreatorOrHost(oAuth2User.getName(), studyId)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        try {
            memberService.deleteMember(studyId, memberId, oAuth2User.getName());
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(null);
        }

        MemberRoleResponse response = memberService.getMyRoleInStudy(oAuth2User.getName(), studyId);
        return ResponseEntity.ok(response);
    }
}
