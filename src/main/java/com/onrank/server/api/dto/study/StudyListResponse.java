package com.onrank.server.api.dto.study;

import com.onrank.server.api.dto.file.FileMetadataDto;
import com.onrank.server.domain.study.Study;
import com.onrank.server.domain.study.StudyStatus;
import lombok.Getter;

@Getter
public class StudyListResponse {

    private Long studyId;
    private String studyName;
    private String studyContent;
    private StudyStatus studyStatus;
    private FileMetadataDto file;

    public StudyListResponse(Study study, FileMetadataDto fileDto) {
        this.studyId = study.getStudyId();
        this.studyName = study.getStudyName();
        this.studyContent = study.getStudyContent();
        this.studyStatus = study.getStudyStatus();
        this.file = fileDto;
    }
}