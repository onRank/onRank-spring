package com.onrank.server.api.controller.assignment;

import com.onrank.server.api.dto.assignment.AddAssignmentRequest;
import com.onrank.server.api.dto.assignment.AssignmentDetailResponse;
import com.onrank.server.api.dto.assignment.AssignmentSummaryResponse;
import com.onrank.server.api.dto.common.ContextResponse;
import com.onrank.server.api.dto.file.PresignedUrlResponse;
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
public class AssignmentController implements AssignmentControllerDocs {

    private final AssignmentService assignmentService;
    private final MemberService memberService;

    /**
     * 과제 업로드 - HOST 또는 CREATOR 만 가능
     */
    @PostMapping
    public ResponseEntity<ContextResponse<List<PresignedUrlResponse>>> createAssignment(
            @PathVariable Long studyId,
            @RequestBody AddAssignmentRequest request,
            @AuthenticationPrincipal CustomOAuth2User oAuth2User) {

        // CREATOR, HOST 만 가능
        if (!memberService.isMemberCreatorOrHost(oAuth2User.getName(), studyId)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        return ResponseEntity.status(HttpStatus.CREATED).body(assignmentService.createAssignment(oAuth2User.getName(), studyId, request));
    }

    /**
     * 과제 목록 조회 - 멤버 기준
     */
    @GetMapping
    public ResponseEntity<ContextResponse<List<AssignmentSummaryResponse>>> getAssignments(
            @PathVariable Long studyId,
            @AuthenticationPrincipal CustomOAuth2User oAuth2User) {

        // 스터디 멤버만 가능
        if (!memberService.isMemberInStudy(oAuth2User.getName(), studyId)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        return ResponseEntity.ok(assignmentService.getAssignments(oAuth2User.getName(), studyId));
    }

    /**
     * 과제 상세 조회 - 멤버 기준
     */
    @GetMapping("/{assignmentId}")
    public ResponseEntity<ContextResponse<AssignmentDetailResponse>> getAssignmentDetail(
            @PathVariable Long studyId,
            @PathVariable Long assignmentId,
            @AuthenticationPrincipal CustomOAuth2User oAuth2User) {

        // 스터디 멤버만 가능
        if (!memberService.isMemberInStudy(oAuth2User.getName(), studyId)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        return ResponseEntity.ok(assignmentService.getAssignmentDetail(oAuth2User.getName(), studyId, assignmentId));
    }
}

// 과제 업로드 (관리자 기준)
// request: 제목, 지시사항, 마감기한, 최대포인트 / 새로 업로드한 과제 파일 List(파일 이름)
// response: 새로 업로드한 과제 파일 List(FileMetadataDto)
// 과제, 제출물 엔티티 생성

// 과제 목록 조회 (멤버 기준)
// request:
// response: 과제 List(과제 ID, 제목, 마감기한, 제출여부, 점수(SCORED))

// 과제 상세 조회 (멤버 기준)
// request:
// response:
// NOTSUBMITTED: 제목, 지시사항, 마감기한, 최대포인트 / 과제 파일 List(FileUrl)
// SUBMITTED: + 제출물 내용 / 제출물 파일 List(FileUrl)
// SCORED: + 점수, 코멘트

// 제출물 업로드 (멤버 기준)
// request: 제출물 내용 / 새로 제출한 제출물 파일 List(파일 이름)
// response: 제출물 파일 List(FileMetadataDto)

// 제출물 수정 (멤버 기준 / SUBMITTED)
// request: 제출물 내용 / 기존 제출물 파일 List(FileMetadataDto), 새로 제출한 제출물 파일 List(파일 이름)
// response: 새로 제출한 제출물 파일 List(FileMetadataDto)

/*--------------------------------------------------*/

// 과제 수정 페이지 (관리자 기준)
// request:
// response: 제목, 지시사항, 마감기한, 최대포인트 / 과제 파일 List(FileMetadataDto)

// 과제 수정 (관리자 기준)
// request: 제목, 지시사항, 마감기한, 최대포인트 / 기존 과제 파일 List(FileMetadataDto) / 새로 업로드한 과제 파일 List(파일 이름)
// response:새로 업로드한 제출물 파일 List(FileMetadataDto)

// 과제 삭제 (관리자 기준)
// request:
// response:

/*--------------------------------------------------*/

// 제출물 목록 조회 (관리자 기준)
// request:
// response: 과제 정보(제목, 마감기한) / 제출물 List(제출물 ID, 멤버 정보(ID, 이름, email), 제출여부, 제출 일시(SUIBMITTED, SCORED), 점수(SCORED))

// 제출물 상세 조회 (관리자 기준 / 채점 페이지 / SUBMITTED, SCORED)
// request:
// response:
// SUBMITTED: 과제 정보(제목, 마감기한) / 멤버 정보(ID, 이름, email) 제출 일시
// SCORED: + 점수, 코멘트

// 제출물 채점 (관리자 기준)
// request: 점수, 코멘트
// response:
// 멤버 엔티티 과제 총점수 속성 업데이트
