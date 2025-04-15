package com.onrank.server.api.dto.common;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Schema(description = "모든 API 응답에 쓰이는 공통 컨텍스트 (사용자 기준 context + API 응답 데이터)")
public class ContextResponse <T> {

    @Schema(description = "사용자 관련 context")
    private MemberStudyContext memberContext;

    @Schema(description = "API 응답 데이터")
    private T data;
}