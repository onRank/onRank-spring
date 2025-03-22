package com.onrank.server.api.dto.notice;

import com.onrank.server.api.dto.file.FileMetadataDto;
import com.onrank.server.domain.notice.Notice;
import lombok.Getter;

import java.time.LocalDate;
import java.util.List;

@Getter
public class NoticeResponse {

    private Long noticeId;
    private String noticeTitle;
    private String noticeContent;
    private LocalDate noticeCreatedAt;
    private LocalDate noticeModifiedAt;
    private List<FileMetadataDto> files;

    public NoticeResponse(Notice notice, List<FileMetadataDto> files) {
        this.noticeId = notice.getNoticeId();
        this.noticeTitle = notice.getNoticeTitle();
        this.noticeContent = notice.getNoticeContent();
        this.noticeCreatedAt = notice.getNoticeCreatedAt();
        this.noticeModifiedAt = notice.getNoticeModifiedAt();
        this.files = files;
    }
}
