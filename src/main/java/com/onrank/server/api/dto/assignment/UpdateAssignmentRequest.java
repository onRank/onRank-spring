package com.onrank.server.api.dto.assignment;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Schema(description = "과제 수정 요청 DTO")
public class UpdateAssignmentRequest {

    @Schema(description = "수정할 과제 제목", example = "퀵정렬 개선 과제")
    private String assignmentTitle;

    @Schema(description = "수정할 과제 지시사항", example = "퀵정렬을 반복문으로도 구현해보세요.")
    private String assignmentContent;

    @Schema(description = "과제 마감일시", example = "2025-05-07T23:59:00")
    private LocalDateTime assignmentDueDate;

    @Schema(description = "과제 배점", example = "120")
    private Integer assignmentMaxPoint;

    @Schema(description = "유지할 기존 파일 ID 목록", example = "[101, 102]")
    private List<Long> remainingFileIds;

    @Schema(description = "새로 업로드할 파일 이름 목록", example = "[\"ImprovedQuickSort.java\"]")
    private List<String> newFileNames;
}
