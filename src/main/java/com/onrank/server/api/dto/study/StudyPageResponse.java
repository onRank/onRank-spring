package com.onrank.server.api.dto.study;

import com.onrank.server.api.dto.member.MemberPointDto;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

import java.util.List;

@Builder
@Schema(description = "스터디 페이지 응답 DTO (랭킹)")
public record StudyPageResponse(
        @Schema(description = "스터디 ID", example = "1")
        Long studyId,

        @Schema(description = "member ID", example = "1")
        Long memberId,

        @Schema(description = "(과제)본인 제출물 점수 총합", example = "10000")
        Long memberSubmissionPoint,

        @Schema(description = "(출석)본인 출석 점수 총합", example = "2000")
        Long memberPresentPoint,

        @Schema(description = "(출석)본인 지각 점수 총합", example = "100")
        Long memberLatePoint,

        @Schema(description = "(출석)본인 결석 점수 총합", example = "10")
        Long memberAbsentPoint,

        @Schema(description = "스터디 멤버 점수 랭킹 목록 (정렬 완료 상태)")
        List<MemberPointDto> memberPointList
) {}