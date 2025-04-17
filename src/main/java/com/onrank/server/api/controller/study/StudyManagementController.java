package com.onrank.server.api.controller.study;

import com.onrank.server.api.dto.common.ContextResponse;
import com.onrank.server.api.dto.common.MemberStudyContext;
import com.onrank.server.api.dto.oauth.CustomOAuth2User;
import com.onrank.server.api.dto.study.*;
import com.onrank.server.api.service.member.MemberService;
import com.onrank.server.api.service.study.StudyService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/studies/{studyId}/management")
@RequiredArgsConstructor
public class StudyManagementController {

    private final StudyService studyService;
    private final MemberService memberService;

    @GetMapping
    public ResponseEntity<ContextResponse<StudyDetailResponse>> getStudyDetail(
            @PathVariable Long studyId,
            @AuthenticationPrincipal CustomOAuth2User oAuth2User) {

        // CREATOR, HOST 만 가능
        if (!memberService.isMemberCreatorOrHost(oAuth2User.getName(), studyId)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        ContextResponse<StudyDetailResponse> response = studyService.getStudyDetail(studyId, oAuth2User.getName());
        return ResponseEntity.ok(response);
    }

    @PutMapping
    public ResponseEntity<ContextResponse<AddStudyResponse>> updateStudy(
            @PathVariable Long studyId,
            @RequestBody StudyUpdateRequest studyUpdateRequest,
            @AuthenticationPrincipal CustomOAuth2User oAuth2User) {

        // CREATOR, HOST 만 가능
        if (!memberService.isMemberCreatorOrHost(oAuth2User.getName(), studyId)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        ContextResponse<AddStudyResponse> response = studyService.updateStudy(studyId, oAuth2User.getName(), studyUpdateRequest);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping
    public ResponseEntity<MemberStudyContext> deleteStudy(
            @PathVariable Long studyId,
            @AuthenticationPrincipal CustomOAuth2User oAuth2User) {

        // CREATOR, HOST 만 가능
        if (!memberService.isMemberCreatorOrHost(oAuth2User.getName(), studyId)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        return ResponseEntity.ok(studyService.deleteStudy(oAuth2User.getName(), studyId));
    }
}
