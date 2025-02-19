package com.onrank.server.api.dto.notice;

import com.onrank.server.domain.notice.Notice;
import lombok.Getter;

import java.time.LocalDate;

@Getter
public class NoticeResponse {

    private Long noticeId;
    private String noticeTitle;
    private String noticeContent;
    private String noticeImagePath;
    private LocalDate noticeCreatedAt;
    private LocalDate noticeModifiedAt;

    public NoticeResponse(Notice notice) {
        this.noticeId = notice.getNoticeId();
        this.noticeTitle = notice.getNoticeTitle();
        this.noticeContent = notice.getNoticeContent();
        this.noticeImagePath = notice.getNoticeImagePath();
        this.noticeCreatedAt = notice.getNoticeCreatedAt();
        this.noticeModifiedAt = notice.getNoticeModifiedAt();
    }
}
