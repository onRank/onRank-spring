package com.onrank.server.api.controller;

import com.onrank.server.api.dto.member.AddMemberRequestDto;
import com.onrank.server.api.dto.member.AddMemberResponseDto;
import com.onrank.server.api.dto.member.MemberResponseDto;
import com.onrank.server.api.service.member.MemberService;
import com.onrank.server.domain.member.Member;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("studies/{studyId}/management")
@RequiredArgsConstructor
public class ManagementController {

    private final MemberService memberService;

    @GetMapping("/member")
    public ResponseEntity<List<MemberResponseDto>> getStudyMembers(@PathVariable Long studyId) {
        List<MemberResponseDto> members = memberService.getMembersForStudy(studyId);
        return ResponseEntity.ok(members);
    }

    @PostMapping("/member/add")
    public ResponseEntity<AddMemberResponseDto> addMember(
            @PathVariable Long studyId,
            @RequestBody AddMemberRequestDto requestDto) {

        Member member = memberService.addMemberToStudy(studyId, requestDto);

        AddMemberResponseDto responseDto = new AddMemberResponseDto(
                member.getMemberId(),
                "Member added successfully"
        );

        return ResponseEntity.status(HttpStatus.CREATED).body(responseDto);
    }
}
