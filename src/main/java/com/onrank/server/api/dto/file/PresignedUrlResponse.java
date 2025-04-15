package com.onrank.server.api.dto.file;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "파일 업로드를 위한 pre-signed URL을 응답할 때 쓰이는 DTO")
public record PresignedUrlResponse(

        @Schema(description = "업로드할 파일 이름", example = "assignment1.pdf")
        String fileName,

        @Schema(description = "S3 업로드용 presigned URL", example = "https://bucket-name.s3...signed-url")
        String uploadUrl
) {}
