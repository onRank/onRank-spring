package com.onrank.server.api.controller.assignment;

import com.onrank.server.api.dto.assignment.AddAssignmentRequest;
import com.onrank.server.api.dto.assignment.AssignmentContext;
import com.onrank.server.api.dto.assignment.AssignmentResponse;
import com.onrank.server.api.dto.file.FileMetadataDto;
import com.onrank.server.api.dto.member.MemberRoleResponse;
import com.onrank.server.api.dto.oauth.CustomOAuth2User;
import com.onrank.server.api.service.assignment.AssignmentService;
import com.onrank.server.api.service.member.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/studies/{studyId}/assignments")
public class AssignmentController {

    private final AssignmentService assignmentService;
    private final MemberService memberService;

    /**
     * 과제 목록 조회 - 현재 로그인된 사용자 기준
     */
    @GetMapping
    public ResponseEntity<AssignmentContext<List<AssignmentResponse>>> getAssignments(
            @PathVariable Long studyId,
            @AuthenticationPrincipal CustomOAuth2User oAuth2User) {

        // 스터디 멤버만 가능
        if (!memberService.isMemberInStudy(oAuth2User.getName(), studyId)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        List<AssignmentResponse> responses = assignmentService.getAssignments(studyId);
        MemberRoleResponse context = memberService.getMyRoleInStudy(oAuth2User.getName(), studyId);

        return ResponseEntity.ok(new AssignmentContext<>(context, responses));
    }

    /**
     * 과제 생성 - HOST 또는 CREATOR 만 가능
     */
    @PostMapping
    public ResponseEntity<AssignmentContext<List<FileMetadataDto>>> createAssignment(
            @PathVariable Long studyId,
            @RequestBody AddAssignmentRequest request,
            @AuthenticationPrincipal CustomOAuth2User oAuth2User) {

        // CREATOR, HOST 만 가능
        if (!memberService.isMemberCreatorOrHost(oAuth2User.getName(), studyId)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        List<FileMetadataDto> fileDtos = assignmentService.createAssignment(studyId, request);
        MemberRoleResponse context = memberService.getMyRoleInStudy(oAuth2User.getName(), studyId);

        return ResponseEntity.status(HttpStatus.CREATED).body(new AssignmentContext<>(context, fileDtos));
    }
}