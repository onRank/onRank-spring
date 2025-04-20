package com.onrank.server.api.dto.submission;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

@Getter
@Schema(description = "제출물 채점 요청 DTO")
public class ScoreSubmissionRequest {

    @Schema(description = "제출물에 부여할 점수", example = "95")
    private Integer submissionScore;

    @Schema(description = "제출물에 대한 피드백 코멘트", example = "테스트 케이스 모두 통과했습니다. 우수한 구현입니다.")
    private String submissionComment;
}
