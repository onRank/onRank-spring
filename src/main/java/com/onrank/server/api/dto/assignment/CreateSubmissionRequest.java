package com.onrank.server.api.dto.assignment;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

import java.util.List;

@Getter
@Schema(description = "과제 제출 요청 DTO")
public class CreateSubmissionRequest {

    @Schema(description = "제출 본문 내용", example = "과제 답안 내용입니다.")
    private String submissionContent;

    @Schema(description = "제출할 파일 이름 리스트", example = "[\"report1.pdf\", \"code.zip\"]")
    private List<String> fileNames;
}
