package com.onrank.server.api.dto.study;

import com.onrank.server.domain.study.Study;
import jakarta.validation.constraints.NotBlank;
import lombok.*;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AddStudyRequest {

    @NotBlank
    private String studyName;
    @NotBlank
    private String studyContent;

    private int presentPoint;
    private int absentPoint;
    private int latePoint;

    private String fileName;

    public Study toEntity() {
        return Study.builder()
                .studyName(studyName)
                .studyContent(studyContent)
                .presentPoint(presentPoint)
                .absentPoint(absentPoint)
                .latePoint(latePoint)
                .build();
    }
}