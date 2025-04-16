package com.onrank.server.api.dto.notice;

import com.onrank.server.domain.notice.Notice;
import com.onrank.server.domain.study.Study;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Getter
@NoArgsConstructor
@Schema(description = "공지사항 생성용 요청 DTO")
public class AddNoticeRequest {

    @Schema(description = "공자사항 제목", example = "1주차 공지사항")
    private String noticeTitle;

    @Schema(description = "공자사항 내용", example = "1주차 공지사항 내용입니다.")
    private String noticeContent;

    @Schema(description = "공지사항 업로드할 파일 이름 목록", example = "[\"곽민서이력서.pdf\", \"README.md\"]")
    private List<String> fileNames;

    public Notice toEntity(Study study) {
        return Notice.builder()
                .noticeTitle(noticeTitle)
                .noticeContent(noticeContent)
                .noticeCreatedAt(LocalDate.now())
                .noticeModifiedAt(LocalDate.now())
                .study(study)
                .build();
    }
}