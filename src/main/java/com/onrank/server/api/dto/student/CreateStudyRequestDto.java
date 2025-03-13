package com.onrank.server.api.dto.student;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CreateStudyRequestDto {

    private String studyName;
    private String studyContent;  // content에서 studyContent로 변경
    private String studyImageUrl; // image에서 studyImageUrl로 변경
    private String studyGoogleFormUrl; // googleForm에서 studyGoogleFormUrl로 변경
}