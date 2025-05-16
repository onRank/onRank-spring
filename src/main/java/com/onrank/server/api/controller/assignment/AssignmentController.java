    package com.onrank.server.api.controller.assignment;

    import com.onrank.server.api.dto.assignment.*;
    import com.onrank.server.api.dto.common.ContextResponse;
    import com.onrank.server.api.dto.file.PresignedUrlResponse;
    import com.onrank.server.api.dto.oauth.CustomOAuth2User;
    import com.onrank.server.api.dto.submission.UpdateSubmissionRequest;
    import com.onrank.server.api.service.assignment.AssignmentService;
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

        /**
         * 과제 업로드 - HOST 또는 CREATOR 만 가능
         */
        @PostMapping
        public ResponseEntity<ContextResponse<List<PresignedUrlResponse>>> createAssignment(
                @PathVariable Long studyId,
                @RequestBody CreateAssignmentRequest request,
                @AuthenticationPrincipal CustomOAuth2User oAuth2User) {
            return ResponseEntity.status(HttpStatus.CREATED).body(assignmentService.createAssignment(oAuth2User.getName(), studyId, request));
        }

        /**
         * 과제 수정 페이지 (관리자 기준)
         */
        @GetMapping("/{assignmentId}/edit")
        public ResponseEntity<ContextResponse<AssignmentEditResponse>> getAssignmentForEdit(
                @PathVariable Long studyId,
                @PathVariable Long assignmentId,
                @AuthenticationPrincipal CustomOAuth2User oAuth2User) {
            try {
                return ResponseEntity.ok(assignmentService.getAssignmentForEdit(oAuth2User.getName(), studyId, assignmentId));
            } catch (IllegalAccessException e) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
            }
        }

        /**
         * 과제 수정 (관리자 기준)
         */
        @PutMapping("/{assignmentId}/edit")
        public ResponseEntity<ContextResponse<List<PresignedUrlResponse>>> updateAssignment(
                @PathVariable Long studyId,
                @PathVariable Long assignmentId,
                @RequestBody UpdateAssignmentRequest request,
                @AuthenticationPrincipal CustomOAuth2User oAuth2User) {

            try {
                return ResponseEntity.ok(assignmentService.updateAssignment(oAuth2User.getName(), studyId, assignmentId, request));
            } catch (IllegalAccessException e) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
            }
        }

        /**
         * 과제 삭제 (관리자 기준)
         */
        @DeleteMapping("/{assignmentId}")
        public ResponseEntity<ContextResponse<Void>> deleteAssignment(
                @PathVariable Long studyId,
                @PathVariable Long assignmentId,
                @AuthenticationPrincipal CustomOAuth2User oAuth2User) {

            try {
                return ResponseEntity.ok(assignmentService.deleteAssignment(oAuth2User.getName(), studyId, assignmentId));
            } catch (IllegalAccessException e) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
            }
        }

        /**
         * 과제 목록 조회 - 멤버 기준
         */
        @GetMapping
        public ResponseEntity<ContextResponse<List<AssignmentListResponse>>> getAssignments(
                @PathVariable Long studyId,
                @AuthenticationPrincipal CustomOAuth2User oAuth2User) {
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
            return ResponseEntity.ok(assignmentService.getAssignmentDetail(oAuth2User.getName(), studyId, assignmentId));
        }

        /**
         * 과제 제출 - 멤버 기준
         */
        @PostMapping("/{assignmentId}")
        public ResponseEntity<ContextResponse<List<PresignedUrlResponse>>> submitAssignment(
                @PathVariable Long studyId,
                @PathVariable Long assignmentId,
                @RequestBody CreateSubmissionRequest request,
                @AuthenticationPrincipal CustomOAuth2User oAuth2User) {
            return ResponseEntity.status((HttpStatus.CREATED)).body(assignmentService.submitAssignment(oAuth2User.getName(), studyId, assignmentId, request));
        }

        /**
         * 과제 재제출
         */
        @PutMapping("/{assignmentId}/resubmit")
        public ResponseEntity<ContextResponse<List<PresignedUrlResponse>>> resubmitAssignment(
                @PathVariable Long studyId,
                @PathVariable Long assignmentId,
                @RequestBody UpdateSubmissionRequest request,
                @AuthenticationPrincipal CustomOAuth2User oAuth2User) throws IllegalAccessException {
            return ResponseEntity.ok(assignmentService.resubmitAssignment(oAuth2User.getName(), studyId, assignmentId, request));
        }
    }