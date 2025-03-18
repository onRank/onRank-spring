package com.onrank.server.api.dto.study;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class MainpageStudyResponseDto {

    private Long studyId;

    private String studyName;

    // studyContent를 content로 변경
    private String studyContent;

    // studyImageUrl을 image로 변경
    private String studyImage;

    // studyGoogleFormUrl을 googleForm으로 변경
    private String studyGoogleForm;
}