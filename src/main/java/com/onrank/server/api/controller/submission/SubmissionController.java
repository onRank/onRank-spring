package com.onrank.server.api.controller.submission;

import com.onrank.server.api.dto.common.ContextResponse;
import com.onrank.server.api.dto.oauth.CustomOAuth2User;
import com.onrank.server.api.dto.submission.ScoreSubmissionRequest;
import com.onrank.server.api.dto.submission.SubmissionDetailResponse;
import com.onrank.server.api.dto.submission.SubmissionListResponse;
import com.onrank.server.api.service.member.MemberService;
import com.onrank.server.api.service.submission.SubmissionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/studies/{studyId}/assignments/{assignmentId}/submissions")
public class SubmissionController {

    private final SubmissionService submissionService;
    private final MemberService memberService;

    /**
     * 제출물 목록 조회 (관리자 기준)
     */
    @GetMapping
    public ResponseEntity<ContextResponse<List<SubmissionListResponse>>> getSubmissions(
            @PathVariable("studyId") Long studyId,
            @PathVariable("assignmentId") Long assignmentId,
            @AuthenticationPrincipal CustomOAuth2User oAuth2User) throws IllegalAccessException {
        return  ResponseEntity.ok(submissionService.getSubmissions(oAuth2User.getName(), studyId, assignmentId));
    }

    /**
     * 제출물 상세 조회 (관리자 기준)
     */
    @GetMapping("/{submissionId}")
    public ResponseEntity<ContextResponse<SubmissionDetailResponse>> getSubmissionDetail(
            @PathVariable("studyId") Long studyId,
            @PathVariable("assignmentId") Long assignmentId,
            @PathVariable("submissionId") Long submissionId,
            @AuthenticationPrincipal CustomOAuth2User oAuth2User) throws IllegalAccessException {
        return  ResponseEntity.ok(submissionService.getSubmissionDetail(oAuth2User.getName(), studyId, assignmentId, submissionId));
    }

    /**
     * 제출물 채점 (관리자 기준)
     */
    @PostMapping("/{submissionId}")
        public ResponseEntity<ContextResponse<Void>> scoreSubmission(
            @PathVariable("studyId") Long studyId,
            @PathVariable("assignmentId") Long assignmentId,
            @PathVariable("submissionId") Long submissionId,
            @RequestBody ScoreSubmissionRequest request,
            @AuthenticationPrincipal CustomOAuth2User oAuth2User) throws IllegalAccessException {
        return ResponseEntity.ok(submissionService.scoreSubmission(oAuth2User.getName(), studyId, assignmentId, submissionId, request));
    }
}
