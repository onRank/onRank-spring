package com.onrank.server.api.dto.notice;

import com.onrank.server.api.dto.file.FileMetadataDto;
import com.onrank.server.domain.notice.Notice;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDate;
import java.util.List;

@Schema(description = "공지사항 상세 조회용 응답 DTO")
public record NoticeDetailResponse (
        @Schema(description = "공자사항 ID", example = "1")
        Long noticeId,

        @Schema(description = "공자사항 제목", example = "1주차 공지사항")
        String noticeTitle,

        @Schema(description = "공자사항 내용", example = "1주차 공지사항 내용입니다.")
        String noticeContent,

        @Schema(description = "공시사항 생성 시간", example = "2025-04-01")
        LocalDate noticeCreatedAt,

        @Schema(description = "공시사항 최종 수정 시간", example = "2025-04-03")
        LocalDate noticeModifiedAt,

        @Schema(description = "공지사항 파일 목록", implementation = FileMetadataDto.class)
        List<FileMetadataDto> files
){
    public static NoticeDetailResponse from (Notice notice, List<FileMetadataDto> files) {
        return new NoticeDetailResponse(
                notice.getNoticeId(),
                notice.getNoticeTitle(),
                notice.getNoticeContent(),
                notice.getNoticeCreatedAt(),
                notice.getNoticeModifiedAt(),
                files
        );
    }
}
