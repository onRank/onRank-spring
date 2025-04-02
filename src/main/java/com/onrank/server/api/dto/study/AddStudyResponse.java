package com.onrank.server.api.dto.study;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class AddStudyResponse {

    private Long studyId;
    private String fileName;
    private String uploadUrl;
}