package com.onrank.server.api.dto.assignment;

import com.onrank.server.api.dto.file.FileMetadataDto;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Builder
@Schema(description = "과제 수정 페이지 응답 DTO")
public class AssignmentEditResponse {

    @Schema(description = "과제 ID", example = "10")
    private Long assignmentId;

    @Schema(description = "과제 제목", example = "퀵정렬 개선 과제")
    private String assignmentTitle;

    @Schema(description = "과제 지시사항", example = "퀵정렬을 반복문으로도 구현해보세요.")
    private String assignmentContent;

    @Schema(description = "과제 마감일시", example = "2025-05-07T23:59:00")
    private LocalDateTime assignmentDueDate;

    @Schema(description = "과제 최대 점수", example = "120")
    private Integer assignmentMaxPoint;

    @Schema(description = "과제 파일 목록", implementation = FileMetadataDto.class)
    private List<FileMetadataDto> assignmentFiles;
}
