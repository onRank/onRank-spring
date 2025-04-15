package com.onrank.server.api.dto.assignment;

import com.onrank.server.domain.assignment.Assignment;
import com.onrank.server.domain.study.Study;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Schema(description = "과제 생성 요청 DTO")
public class AddAssignmentRequest {

    @Schema(description = "과제 제목", example = "퀵정렬 구현 과제")
    private String assignmentTitle;

    @Schema(description = "과제 설명", example = "퀵정렬 알고리즘을 재귀로 구현하고 시간복잡도를 분석하세요.")
    private String assignmentContent;

    @Schema(description = "과제 마감 기한", example = "2025-05-01T23:59:00")
    private LocalDateTime assignmentDueDate;

    @Schema(description = "과제 배점", example = "100")
    private Long assignmentMaxPoint;

    @Schema(description = "업로드할 파일 이름 목록", example = "[\"QuickSort.java\", \"README.md\"]")
    private List<String> fileNames;

    public Assignment toEntity(Study study) {
        return Assignment.builder()
                .study(study)
                .assignmentTitle(assignmentTitle)
                .assignmentContent(assignmentContent)
                .assignmentCreatedAt(LocalDate.now())
                .assignmentDueDate(assignmentDueDate)
                .assignmentMaxPoint(assignmentMaxPoint)
                .build();
    }
}