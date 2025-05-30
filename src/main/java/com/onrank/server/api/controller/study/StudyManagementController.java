package com.onrank.server.api.controller.study;

import com.onrank.server.api.dto.common.ContextResponse;
import com.onrank.server.api.dto.file.PresignedUrlResponse;
import com.onrank.server.api.dto.auth.CustomOAuth2User;
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
public class StudyManagementController implements StudyManagementControllerDocs {

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
        ContextResponse<StudyDetailResponse> response = studyService.getStudyDetail(oAuth2User.getName(), studyId);
        return ResponseEntity.ok(response);
    }

    @PutMapping
    public ResponseEntity<ContextResponse<PresignedUrlResponse>> updateStudy(
            @PathVariable Long studyId,
            @RequestBody StudyUpdateRequest studyUpdateRequest,
            @AuthenticationPrincipal CustomOAuth2User oAuth2User) {
        return ResponseEntity.ok(studyService.updateStudy(oAuth2User.getName(), studyId, studyUpdateRequest));
    }

    @DeleteMapping
    public ResponseEntity<Void> deleteStudy(
            @PathVariable Long studyId,
            @AuthenticationPrincipal CustomOAuth2User oAuth2User) {

        studyService.deleteStudy(oAuth2User.getName(), studyId);
        return ResponseEntity.noContent().build();
    }
}
