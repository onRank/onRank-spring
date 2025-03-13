package com.onrank.server.api.dto.student;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter  // Setter 추가
@NoArgsConstructor  // 기본 생성자 추가
@AllArgsConstructor  // 모든 필드를 파라미터로 받는 생성자 추가
public class CreateStudyRequestDto {

    private String studyName;
    private String content;  // 소문자로 시작하는 camelCase로 변경
    private String image;    // API 문서에 맞게 imageUrl → image로 변경
    private String googleForm;  // API 문서에 맞게 googleFormUrl → googleForm으로 변경
}