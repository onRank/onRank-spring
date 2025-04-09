package com.onrank.server.api.dto.study;

import com.onrank.server.domain.study.StudyStatus;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class StudyUpdateRequest {

    private String studyName;
    private String studyContent;
    private int presentPoint;
    private int absentPoint;
    private int latePoint;
    private StudyStatus studyStatus;  // 예: PROGRESS, COMPLETED

    private String fileName;
}
