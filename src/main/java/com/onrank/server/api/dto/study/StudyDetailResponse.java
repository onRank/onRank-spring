package com.onrank.server.api.dto.study;

import com.onrank.server.domain.study.Study;
import com.onrank.server.domain.study.StudyStatus;
import lombok.Getter;

@Getter
public class StudyDetailResponse {
    private Long studyId;
    private String studyName;
    private String studyContent;
    private int presentPoint;
    private int absentPoint;
    private int latePoint;
    private StudyStatus studyStatus;

    public StudyDetailResponse(Study study) {
        this.studyId = study.getStudyId();
        this.studyName = study.getStudyName();
        this.studyContent = study.getStudyContent();
        this.presentPoint = study.getPresentPoint();
        this.absentPoint = study.getAbsentPoint();
        this.latePoint = study.getLatePoint();
        this.studyStatus = study.getStudyStatus();
    }
}
