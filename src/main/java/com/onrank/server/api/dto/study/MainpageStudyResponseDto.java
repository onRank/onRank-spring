package com.onrank.server.api.dto.study;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class MainpageStudyResponseDto {

    private String studyName;

    // studyContent를 content로 변경
    private String content;

    // studyImageUrl을 image로 변경
    private String image;

    // studyGoogleFormUrl을 googleForm으로 변경
    private String googleForm;
}