package com.onrank.server.api.dto.student;

import lombok.Getter;

@Getter
public class CreateStudyRequestDto {

    private String studyName;
    private String studyContent;
    private String studyImageUrl;
    private String studyGoogleFormUrl;
}
