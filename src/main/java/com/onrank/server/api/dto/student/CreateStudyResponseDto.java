package com.onrank.server.api.dto.student;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class CreateStudyResponseDto {

    private Long studyId;
    private String message;
}