package com.onrank.server.api.dto.notice;

import com.onrank.server.domain.notice.Notice;
import com.onrank.server.domain.study.Study;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Getter
@NoArgsConstructor
public class AddNoticeRequest {

    private String title;
    private String content;
    private String imagePath;

    public Notice toEntity(Study study) {
        return Notice.builder()
                .title(title)
                .content(content)
                .createdAt(LocalDate.now())
                .modifiedAt(LocalDate.now())
                .imagePath(imagePath)
                .study(study)
                .build();
    }
}
