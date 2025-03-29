package com.onrank.server.api.controller;

import com.onrank.server.api.dto.member.AddMemberRequest;
import com.onrank.server.api.dto.member.MemberListResponse;
import com.onrank.server.api.dto.oauth.CustomOAuth2User;
import com.onrank.server.api.service.member.MemberService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("studies/{studyId}/management")
@RequiredArgsConstructor
public class MemberController {

    private final MemberService memberService;

    @GetMapping("/members")
    public ResponseEntity<List<MemberListResponse>> getMembers(
            @PathVariable Long studyId,
            @AuthenticationPrincipal CustomOAuth2User oAuth2User) {

        // HOST 만 가능
        if (!memberService.isMemberHost(oAuth2User.getName(), studyId)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        return ResponseEntity.ok(memberService.getMembersForStudy(studyId));
    }

    @PostMapping("/members/add")
    public ResponseEntity<Void> addMember(
            @PathVariable Long studyId,
            @RequestBody AddMemberRequest addMemberRequest,
            @AuthenticationPrincipal CustomOAuth2User oAuth2User) {

        // HOST 만 가능
        if (!memberService.isMemberHost(oAuth2User.getName(), studyId)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        memberService.addMemberToStudy(studyId, addMemberRequest);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }
}
