package com.onrank.server.api.service.notice;

import com.onrank.server.api.dto.common.ContextResponse;
import com.onrank.server.api.dto.common.MemberStudyContext;
import com.onrank.server.api.dto.file.FileMetadataDto;
import com.onrank.server.api.dto.notice.AddNoticeRequest;
import com.onrank.server.api.dto.notice.NoticeListResponse;
import com.onrank.server.api.dto.notice.NoticeDetailResponse;
import com.onrank.server.api.service.file.FileService;
import com.onrank.server.api.service.member.MemberService;
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
    private final FileService fileService;
    private final MemberService memberService;

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
        fileService.createMultiplePresignedUrls(FileCategory.NOTICE, notice.getNoticeId(), addNoticeRequest.getFileNames());

        // 메타데이터를 다시 조회하여 DTO 생성
        List<FileMetadata> metadataList = fileService.findFile(FileCategory.NOTICE, notice.getNoticeId());

        return metadataList.stream()
                .map(file -> new FileMetadataDto(file, fileService.getBucketName()))
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
        List<FileMetadata> existingFiles = fileService.findFile(FileCategory.NOTICE, noticeId);
        existingFiles.forEach(file -> fileService.deleteFile(file.getFileKey()));
        fileService.deleteFileMetadata(FileCategory.NOTICE, noticeId);

        // 새 파일 presigned URL 발급 및 메타데이터 저장
        fileService.createMultiplePresignedUrls(FileCategory.NOTICE, noticeId, request.getFileNames());

        // 메타데이터 다시 조회하여 DTO 생성
        List<FileMetadata> newFiles = fileService.findFile(FileCategory.NOTICE, noticeId);

        return newFiles.stream()
                .map(file -> new FileMetadataDto(file, fileService.getBucketName()))
                .collect(Collectors.toList());
    }

    // 공지사항 삭제
    @Transactional
    public void deleteNotice(Long noticeId) {
        Notice notice = noticeRepository.findByNoticeId(noticeId)
                .orElseThrow(() -> new IllegalArgumentException("Notice not found"));

        // S3 파일 및 메타데이터 삭제
        List<FileMetadata> files = fileService.findFile(FileCategory.NOTICE, noticeId);
        files.forEach(file -> {
            fileService.deleteFile(file.getFileKey());
        });

        noticeRepository.delete(notice);
    }

    // 공지사항 상세 조회
    public ContextResponse<NoticeDetailResponse> getNoticeDetail(String username, Long studyId, Long noticeId) {
        Notice notice = noticeRepository.findByNoticeId(noticeId)
                .orElseThrow(() -> new IllegalArgumentException("Notice not found"));

        List<FileMetadataDto> fileDtos = fileService.getMultipleFileMetadata(FileCategory.NOTICE, noticeId);

        MemberStudyContext context = memberService.getContext(username, studyId);
        return new ContextResponse<>(context, NoticeDetailResponse.from(notice, fileDtos));
    }

    // 공지사항 목록 조회
    public ContextResponse<List<NoticeListResponse>> getNotices(String username, Long studyId) {

        List<NoticeListResponse> responses = noticeRepository.findByStudyStudyId(studyId)
                .stream()
                .map(NoticeListResponse::from)
                .toList();

        MemberStudyContext context = memberService.getContext(username, studyId);
        return new ContextResponse<>(context, responses);
    }
}
