package com.onrank.server.api.dto.submission;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

import java.util.List;

@Getter
@Schema(description = "제출물 수정(재제출) 요청 DTO")
public class UpdateSubmissionRequest {

    @Schema(description = "제출물 본문 내용", example = "코드를 최적화했습니다. 시간복잡도가 개선되었습니다.")
    private String submissionContent;

    @Schema(description = "유지할 기존 파일 ID 목록", example = "[201, 202]")
    private List<Long> remainingFileIds;

    @Schema(description = "새로 제출할 파일 이름 목록", example = "[\"OptimizedQuickSort.java\"]")
    private List<String> newFileNames;
}
