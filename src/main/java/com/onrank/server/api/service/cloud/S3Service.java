package com.onrank.server.api.service.cloud;

import com.onrank.server.domain.file.FileCategory;
import com.onrank.server.domain.file.FileMetadata;
import com.onrank.server.domain.file.FileMetadataJpaRepository;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.PresignedPutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.model.PutObjectPresignRequest;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Getter
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class S3Service {

    private final S3Client s3Client;
    private final S3Presigner s3Presigner;
    private final FileMetadataJpaRepository fileMetadataRepository;

    @Value("${cloud.aws.s3.bucket}")
    private String bucketName;

    /**
     * 파일 1개 업로드: Pre-signed URL 발급 및 파일 메타데이터 저장
     */
    @Transactional
    public String generatePresignedUrl(FileCategory category, Long entityId, String fileName) {
        String uniqueFileName = category.name().toLowerCase() + "/" + entityId + "/" +  UUID.randomUUID() + "_" + fileName;

        PutObjectPresignRequest request = PutObjectPresignRequest.builder()
                .signatureDuration(Duration.ofMinutes(10)) // 10분 유효
                .putObjectRequest(builder -> builder
                        .bucket(bucketName)
                        .key(uniqueFileName)
                        .build())
                .build();

        PresignedPutObjectRequest presignedRequest = s3Presigner.presignPutObject(request);

        // file metadata 정보를 데이터베이스에 저장
        FileMetadata fileMetadata = FileMetadata.builder()
                .category(category)
                .entityId(entityId)
                .fileName(fileName)
                .filePath(uniqueFileName)
                .build();
        fileMetadataRepository.save(fileMetadata);

        return presignedRequest.url().toString();
    }

    /**
     * 여러 파일 업로드: Pre-signed URL 발급 및 메타데이터 일괄 저장
     */
    @Transactional
    public List<Map<String, String>> uploadFilesWithMetadata(
            FileCategory category, Long entityId, List<String> fileNames) {

        if(fileNames == null || fileNames.isEmpty()) return List.of();

        List<Map<String, String>> presignedUrls = new ArrayList<>();

        for (String fileName : fileNames) {
            String uploadUrl = generatePresignedUrl(category, entityId, fileName);
            presignedUrls.add(Map.of(
                    "fileName", fileName,
                    "uploadUrl", uploadUrl
            ));
        }
        return presignedUrls;
    }

    /**
     * 특정 엔티티(Post, Notice 등)의 모든 파일 조회
     */
    public List<FileMetadata> findFile(FileCategory category, Long entityId) {
        return fileMetadataRepository.findByCategoryAndEntityId(category, entityId);
    }

    /**
     * 파일 삭제 (단일 파일 S3에서 삭제)
     */
    @Transactional
    public void deleteFile(String filePath) {
        s3Client.deleteObject(DeleteObjectRequest.builder()
                .bucket(bucketName)
                .key(filePath)
                .build());
    }

    @Transactional
    public void deleteFileMetadata(FileCategory category, Long entityId) {
        List<FileMetadata> files = fileMetadataRepository.findByCategoryAndEntityId(category, entityId);
        fileMetadataRepository.deleteAll(files);
    }
}