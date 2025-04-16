package com.onrank.server.api.dto.notice;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
@Schema(description = "공지사항 수정용 요청 DTO")
public class UpdateNoticeRequest {

    @Schema(description = "공자사항 제목 수정", example = "1주차 공지사항")
    private String noticeTitle;

    @Schema(description = "공자사항 내용 수정", example = "1주차 공지사항 내용입니다.")
    private String noticeContent;

    @Schema(description = "남아있는 파일 Id 목록", example = "[\"123\", \"14141\"]")
    private List<Long> remainingFileIds;

    @Schema(description = "신규 파일 이름 목록", example = "[\"신규파일이름.pdf\", \"곽민서.pdf\"]")
    private List<String> newFileNames;
}