package com.onrank.server.api.service.notice;

import com.onrank.server.api.dto.file.FileMetadataDto;
import com.onrank.server.api.dto.notice.AddNoticeRequest;
import com.onrank.server.api.dto.notice.NoticeResponse;
import com.onrank.server.api.service.cloud.S3Service;
import com.onrank.server.domain.file.FileCategory;
import com.onrank.server.domain.file.FileMetadata;
import com.onrank.server.domain.notice.Notice;
import com.onrank.server.domain.notice.NoticeJpaRepository;
import com.onrank.server.domain.study.Study;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class NoticeService {

    private final NoticeJpaRepository noticeRepository;
    private final S3Service s3Service;

    public Optional<Notice> findByNoticeId(Long noticeId) {
        return noticeRepository.findByNoticeId(noticeId);
    }

    public List<Notice> findByStudyId(Long studyId) {
        return noticeRepository.findByStudyStudyId(studyId);
    }

    @Transactional
    public List<FileMetadataDto> createNotice(AddNoticeRequest addNoticeRequest, Study study) {
        // 공지 생성 및 저장
        Notice notice = addNoticeRequest.toEntity(study);
        noticeRepository.save(notice);

        // 파일 presigned URL 발급 및 메타데이터 저장
        s3Service.uploadFilesWithMetadata(FileCategory.NOTICE, notice.getNoticeId(), addNoticeRequest.getFileNames());

        // 메타데이터를 다시 조회하여 DTO 생성
        List<FileMetadata> metadataList = s3Service.findFile(FileCategory.NOTICE, notice.getNoticeId());

        return metadataList.stream()
                .map(file -> new FileMetadataDto(file, s3Service.getBucketName()))
                .collect(Collectors.toList());
    }

    // 공지사항 수정
    @Transactional
    public List<FileMetadataDto> updateNotice(Long noticeId, AddNoticeRequest request) {
        // 공지 엔티티 조회 및 내용 수정
        Notice notice = noticeRepository.findByNoticeId(noticeId)
                .orElseThrow(() -> new IllegalArgumentException("Notice not found"));

        notice.update(request.getNoticeTitle(), request.getNoticeContent());

        // 기존 파일과 메타데이터 모두 삭제
        List<FileMetadata> existingFiles = s3Service.findFile(FileCategory.NOTICE, noticeId);
        existingFiles.forEach(file -> s3Service.deleteFile(file.getFilePath()));
        s3Service.deleteFileMetadata(FileCategory.NOTICE, noticeId);

        // 새 파일 presigned URL 발급 및 메타데이터 저장
        s3Service.uploadFilesWithMetadata(FileCategory.NOTICE, noticeId, request.getFileNames());

        // 메타데이터 다시 조회하여 DTO 생성
        List<FileMetadata> newFiles = s3Service.findFile(FileCategory.NOTICE, noticeId);

        return newFiles.stream()
                .map(file -> new FileMetadataDto(file, s3Service.getBucketName()))
                .collect(Collectors.toList());
    }

    // 공지사항 삭제
    @Transactional
    public void deleteNotice(Long noticeId) {
        Notice notice = noticeRepository.findByNoticeId(noticeId)
                .orElseThrow(() -> new IllegalArgumentException("Notice not found"));

        // S3 파일 및 메타데이터 삭제
        List<FileMetadata> files = s3Service.findFile(FileCategory.NOTICE, noticeId);
        files.forEach(file -> {
            s3Service.deleteFile(file.getFilePath());
        });

        noticeRepository.delete(notice);
    }

    // 공지사항 상세 조회를 위한 NoticeResponse 객체 생성
    public NoticeResponse getNoticeResponse(Long noticeId) {
        Notice notice = noticeRepository.findByNoticeId(noticeId)
                .orElseThrow(() -> new IllegalArgumentException("Notice not found"));

        List<FileMetadata> files = s3Service.findFile(FileCategory.NOTICE, noticeId);
        List<FileMetadataDto> fileDtos = files.stream()
                .map(file -> new FileMetadataDto(file, s3Service.getBucketName()))
                .collect(Collectors.toList());

        return new NoticeResponse(notice, fileDtos);
    }

    // 공지사항 목록 조회를 위한 List<NoticeResponse> 객체 생성
    public List<NoticeResponse> getNoticeResponsesByStudyId(Long studyId) {
        return noticeRepository.findByStudyStudyId(studyId)
                .stream()
                .map(notice -> {
                    List<FileMetadata> files = s3Service.findFile(FileCategory.NOTICE, notice.getNoticeId());
                    List<FileMetadataDto> fileDtos = files.stream()
                            .map(file -> new FileMetadataDto(file, s3Service.getBucketName()))
                            .collect(Collectors.toList());

                    return new NoticeResponse(notice, fileDtos);
                })
                .collect(Collectors.toList());
    }
}
