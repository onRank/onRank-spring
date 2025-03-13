package com.onrank.server.api.dto.study;


import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class MainpageStudyResponseDto {

    private String studyName;
    private String studyContent;
    private String StudyImageUrl;
}
