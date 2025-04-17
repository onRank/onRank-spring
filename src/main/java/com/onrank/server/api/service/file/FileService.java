package com.onrank.server.api.service.file;

import com.onrank.server.api.dto.file.FileMetadataDto;
import com.onrank.server.api.dto.file.PresignedUrlResponse;
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

import java.net.URL;
import java.time.Duration;
import java.util.List;
import java.util.UUID;

@Getter
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class FileService {

    private final S3Client s3Client;
    private final S3Presigner s3Presigner;
    private final FileMetadataJpaRepository fileMetadataRepository;

    @Value("${cloud.aws.s3.bucket}")
    private String bucketName;

    /**
     * 파일 1개 업로드: Pre-signed URL 발급 및 파일 메타데이터 저장
     */
    @Transactional
    public String createPresignedUrlAndSaveMetadata(FileCategory category, Long entityId, String fileName) {
        String fileKey = category.name().toLowerCase() + "/" + entityId + "/" +  UUID.randomUUID() + "_" + fileName;

        PutObjectPresignRequest request = PutObjectPresignRequest.builder()
                .signatureDuration(Duration.ofMinutes(10)) // 10분 유효
                .putObjectRequest(builder -> builder
                        .bucket(bucketName)
                        .key(fileKey)
                        .build())
                .build();

        // file metadata 정보를 데이터베이스에 저장
        FileMetadata fileMetadata = FileMetadata.builder()
                .category(category)
                .entityId(entityId)
                .fileName(fileName)
                .fileKey(fileKey)
                .build();
        fileMetadataRepository.save(fileMetadata);

//        URL presignedUrl = s3Presigner.presignPutObject(request).url();
//        return presignedUrl.toString();
        return s3Presigner.presignPutObject(request).url().toString();
    }


    /**
     * 여러 파일 업로드: Pre-signed URL 발급 및 메타데이터 일괄 저장
     */
    @Transactional
    public List<PresignedUrlResponse> createMultiplePresignedUrls(
            FileCategory category, Long entityId, List<String> fileNames) {

        if(fileNames == null || fileNames.isEmpty()) return List.of();

        return fileNames.stream()
                .map(fileName -> new PresignedUrlResponse(fileName, createPresignedUrlAndSaveMetadata(category, entityId, fileName)))
                .toList();
    }

    /**
     * 특정 엔티티(Post, Notice 등)의 모든 파일 조회
     */
    public List<FileMetadataDto> getMultipleFileMetadata(FileCategory category, Long entityId) {
        List<FileMetadata> fileMetadataList = fileMetadataRepository.findByCategoryAndEntityId(category, entityId);

        return fileMetadataList.stream()
                .map(fileMetadata -> new FileMetadataDto(fileMetadata, bucketName))
                .toList();
    }

    /**
     * 특정 엔티티(Post, Notice 등)의 모든 파일 조회
     */
    public List<FileMetadata> findFile(FileCategory category, Long entityId) {
        return fileMetadataRepository.findByCategoryAndEntityId(category, entityId);
    }

    // S3 파일 삭제
    @Transactional
    public void deleteFile(String filePath) {
        s3Client.deleteObject(DeleteObjectRequest.builder()
                .bucket(bucketName)
                .key(filePath)
                .build());
    }

    // 해당 Entity 의 모든 파일 삭제 (S3 & DB)
    @Transactional
    public void deleteAllFilesAndMetadata(FileCategory category, Long entityId) {
        List<FileMetadata> files = fileMetadataRepository.findByCategoryAndEntityId(category, entityId);

        // S3에서 삭제
        for (FileMetadata file : files) {
            deleteFile(file.getFileKey());
        }

        // 메타데이터 삭제
        fileMetadataRepository.deleteAll(files);
    }

    // 단일 파일 수정 (Study 이미지용)
    @Transactional
    public PresignedUrlResponse replaceStudyFile(Long studyId, String newFileName) {

        // 기존 스터디 사진 삭제
        fileMetadataRepository.findByCategoryAndEntityId(FileCategory.STUDY, studyId).stream()
                .findFirst()
                .ifPresent(file -> {
                    deleteFile(file.getFileKey());
                    fileMetadataRepository.delete(file);
                });

        return new PresignedUrlResponse(newFileName, createPresignedUrlAndSaveMetadata(FileCategory.STUDY, studyId, newFileName));
    }

    // 다중 파일 수정
    @Transactional
    public List<PresignedUrlResponse> replaceFiles(
            FileCategory category,
            Long entityId,
            List<Long> remainingFileIds, // 남길 파일
            List<String> newFileNames) { // 신규 업로드할 파일

        // 기존 모든 파일 조회
        List<FileMetadata> existingFiles = fileMetadataRepository.findByCategoryAndEntityId(category, entityId);

        // 삭제할 파일 필터링
        List<FileMetadata> toDelete = existingFiles.stream()
                .filter(file -> !remainingFileIds.contains(file.getFileId()))
                .toList();

        // S3 및 DB 에서 삭제
        for (FileMetadata file : toDelete) {
            deleteFile(file.getFileKey());
            fileMetadataRepository.delete(file);
        }

        // 신규 파일 업로드 URL 발급 및 메타데이터 저장
        return createMultiplePresignedUrls(category, entityId, newFileNames);
    }
}