package com.onrank.server.api.controller;

import com.onrank.server.api.dto.member.AddMemberRequest;
import com.onrank.server.api.dto.member.MemberListResponse;
import com.onrank.server.api.dto.member.MemberRoleRequest;
import com.onrank.server.api.dto.oauth.CustomOAuth2User;
import com.onrank.server.api.service.member.MemberService;
import com.onrank.server.api.service.student.StudentService;
import com.onrank.server.domain.member.Member;
import com.onrank.server.domain.member.MemberRole;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;

@Slf4j
@RestController
@RequestMapping("studies/{studyId}/management/members")
@RequiredArgsConstructor
public class MemberController {

    private final MemberService memberService;
    private final StudentService studentService;

    @GetMapping()
    public ResponseEntity<List<MemberListResponse>> getMembers(
            @PathVariable Long studyId,
            @AuthenticationPrincipal CustomOAuth2User oAuth2User) {

        // HOST 만 가능
        if (!memberService.isMemberHost(oAuth2User.getName(), studyId)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        return ResponseEntity.ok(memberService.getMembersForStudy(studyId));
    }

    @PostMapping("/add")
    public ResponseEntity<Void> addMember(
            @PathVariable Long studyId,
            @RequestBody AddMemberRequest addMemberRequest,
            @AuthenticationPrincipal CustomOAuth2User oAuth2User) {

        // HOST 만 가능
        if (!memberService.isMemberHost(oAuth2User.getName(), studyId)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        // 서비스에 존재 여부
        if (studentService.checkIfExist(addMemberRequest.getStudentEmail())) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

        memberService.addMemberToStudy(studyId, addMemberRequest);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PutMapping("/{memberId}/role")
    public ResponseEntity<Void> updateMemberRole(
            @PathVariable Long studyId,
            @PathVariable Long memberId,
            @RequestBody MemberRoleRequest memberRoleRequest,
            @AuthenticationPrincipal CustomOAuth2User oAuth2User) {

        // HOST 만 가능
        if (!memberService.isMemberHost(oAuth2User.getName(), studyId)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        memberService.updateMemberRole(studyId, memberId, memberRoleRequest.getMemberRole());
        return ResponseEntity.noContent().build();
    }
}
