package com.onrank.server.api.dto.notice;

import com.onrank.server.domain.notice.Notice;
import com.onrank.server.domain.study.Study;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Getter
@NoArgsConstructor
public class AddNoticeRequest {

    @NotBlank
    @Size(max = 255)
    private String noticeTitle;

    @NotBlank
    private String noticeContent;

    private String noticeImagePath;

    public Notice toEntity(Study study) {
        return Notice.builder()
                .noticeTitle(noticeTitle)
                .noticeContent(noticeContent)
                .noticeCreatedAt(LocalDate.now())
                .noticeModifiedAt(LocalDate.now())
                .noticeImagePath(noticeImagePath)
                .study(study)
                .build();
    }
}