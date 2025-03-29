package com.onrank.server.api.dto.study;

import com.onrank.server.api.dto.file.FileMetadataDto;
import com.onrank.server.domain.study.Study;
import lombok.Getter;

@Getter
public class StudyListResponse {

    private Long studyId;
    private String studyName;
    private String studyContent;
    private FileMetadataDto file;

    public StudyListResponse(Study study, FileMetadataDto fileDto) {
        this.studyId = study.getStudyId();
        this.studyName = study.getStudyName();
        this.studyContent = study.getStudyContent();
        this.file = fileDto;
    }
}