package com.onrank.server.api.dto.file;

import com.onrank.server.domain.file.FileMetadata;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

@Getter
public class FileMetadataDto {

    @Schema(description = "파일 ID", example = "101")
    private final Long fileId;

    @Schema(description = "파일 이름", example = "study_image.png")
    private final String fileName;

    @Schema(description = "S3 파일 URL", example = "https://bucket-name.s3.ap-northeast-2.amazonaws.com/study/1/uuid_study_image.png")
    private final String fileUrl;

    public FileMetadataDto(FileMetadata fileMetadata, String bucketName) {
        this.fileId = fileMetadata.getFileId();
        this.fileName = fileMetadata.getFileName();
        if(this.fileName != null) {
            this.fileUrl = "https://" + bucketName + ".s3.ap-northeast-2.amazonaws.com/" + fileMetadata.getFileKey();
        } else {
            this.fileUrl = null;
        }
    }
}
