package com.onrank.server.api.dto.student;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class AddStudyResponse {

    private Long studyId;
    private String fileName;
    private String uploadUrl;


}