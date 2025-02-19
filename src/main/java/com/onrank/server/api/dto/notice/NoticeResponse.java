package com.onrank.server.api.dto.notice;

import com.onrank.server.domain.notice.Notice;
import lombok.Getter;

import java.time.LocalDate;

@Getter
public class NoticeResponse {

    private Long id;
    private String title;
    private String content;
    private String imagePath;
    private LocalDate createdAt;
    private LocalDate modifiedAt;

    public NoticeResponse(Notice notice) {
        this.id = notice.getId();
        this.title = notice.getTitle();
        this.content = notice.getContent();
        this.imagePath = notice.getImagePath();
        this.createdAt = notice.getCreatedAt();
        this.modifiedAt = notice.getModifiedAt();
    }
}
