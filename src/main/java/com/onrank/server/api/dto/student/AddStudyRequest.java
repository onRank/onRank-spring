package com.onrank.server.api.dto.student;

import com.onrank.server.domain.notice.Notice;
import com.onrank.server.domain.student.Student;
import com.onrank.server.domain.study.Study;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;

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