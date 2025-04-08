package com.onrank.server.api.dto.assignment;

import com.onrank.server.domain.assignment.Assignment;
import com.onrank.server.domain.study.Study;
import lombok.Getter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Getter
public class AddAssignmentRequest {

    private String assignmentTitle;
    private String assignmentContent;
    private LocalDateTime assignmentDueDate;

    // 업로드하기 위한 파일명들
    private List<String> fileNames;

    public Assignment toEntity(Study study) {
        return Assignment.builder()
                .study(study)
                .assignmentTitle(assignmentTitle)
                .assignmentContent(assignmentContent)
                .assignmentCreatedAt(LocalDate.now())
                .assignmentDueDate(assignmentDueDate)
                .build();
    }
}
