package com.onrank.server.api.dto.study;

import com.onrank.server.domain.study.Study;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AddStudyRequest {

    @NotBlank
    private String studyName;
    @NotBlank
    private String studyContent;

    private String fileName;
    private String studyGoogleFormUrl;

    public Study toEntity() {
        return Study.builder()
                .studyName(studyName)
                .studyContent(studyContent)
                .studyGoogleFormUrl(studyGoogleFormUrl)
                .build();
    }
}