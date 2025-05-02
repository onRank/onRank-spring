package com.onrank.server.api.dto.submission;

import com.onrank.server.domain.member.Member;
import com.onrank.server.domain.submission.Submission;
import com.onrank.server.domain.submission.SubmissionStatus;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;

@Schema(description = "제출물 목록 조회용 응답 DTO")
public record SubmissionListResponse(

        @Schema(description = "제출물 ID", example = "101")
        Long submissionId,

        @Schema(description = "멤버 ID", example = "501")
        Long memberId,

        @Schema(description = "멤버 이름", example = "홍길동")
        String memberName,

        @Schema(description = "멤버 이메일", example = "hong@univ.ac.kr")
        String memberEmail,

        @Schema(description = "제출 상태", example = "SUBMITTED")
        SubmissionStatus submissionStatus,

        @Schema(description = "제출 일시", example = "2025-04-20T13:00:12")
        LocalDateTime submissionCreatedAt,

        @Schema(description = "점수 (SCORED 상태만)", example = "95")
        Integer submissionScore
) {
    public static SubmissionListResponse from(Submission submission) {
        Member member = submission.getMember();
        return new SubmissionListResponse(
                submission.getSubmissionId(),
                member.getMemberId(),
                member.getStudent().getStudentName(),
                member.getStudent().getStudentEmail(),
                submission.getSubmissionStatus(),
                submission.getSubmissionCreatedAt(),
                submission.getSubmissionScore()
        );
    }
}