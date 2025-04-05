package com.onrank.server.api.dto.file;

import com.onrank.server.domain.file.FileMetadata;
import lombok.Getter;

@Getter
public class FileMetadataDto {
    private Long fileId;
    private String fileName;
    private String fileUrl;

    public FileMetadataDto(FileMetadata fileMetadata, String bucketName) {
        this.fileId = fileMetadata.getFileId();
        this.fileName = fileMetadata.getFileName();
        this.fileUrl = "https://" + bucketName + ".s3.ap-northeast-2.amazonaws.com/" + fileMetadata.getFilePath();
    }

    public FileMetadataDto(String fileName, String fileUrl) {
        this.fileName = fileName;
        this.fileUrl = fileUrl;
    }
}
