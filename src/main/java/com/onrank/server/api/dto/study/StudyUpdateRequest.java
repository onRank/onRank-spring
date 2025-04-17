package com.onrank.server.api.dto.study;

import com.onrank.server.domain.study.StudyStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@Schema(description = "스터디 수정용 요청 DTO")
public class StudyUpdateRequest {

    @Schema(description = "스터디 이름 수정", example = "임찬우의 스터디")
    private String studyName;

    @Schema(description = "스터디 설명 수정", example = "자바스크립트 스터디 설명")
    private String studyContent;

    @Schema(description = "출석 point 수정", example = "100")
    private int presentPoint;

    @Schema(description = "결석 point 수정", example = "0")
    private int absentPoint;

    @Schema(description = "지각 point 수정", example = "50")
    private int latePoint;

    @Schema(description = "스터디 진행 상태 수정", example = "PROGRESS, COMPLETED")
    private StudyStatus studyStatus;  // 예: PROGRESS, COMPLETED

    @Schema(description = "신규 파일의 이름\n - 변경사항 존재시\n -기존 파일은 삭제", example = "신규파일이름.png")
    private String newFileName;
}
