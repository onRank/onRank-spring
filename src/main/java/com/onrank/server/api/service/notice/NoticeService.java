package com.onrank.server.api.service.notice;

import com.onrank.server.api.dto.common.ContextResponse;
import com.onrank.server.api.dto.common.MemberStudyContext;
import com.onrank.server.api.dto.file.FileMetadataDto;
import com.onrank.server.api.dto.file.PresignedUrlResponse;
import com.onrank.server.api.dto.notice.AddNoticeRequest;
import com.onrank.server.api.dto.notice.NoticeListResponse;
import com.onrank.server.api.dto.notice.NoticeDetailResponse;
import com.onrank.server.api.dto.notice.UpdateNoticeRequest;
import com.onrank.server.api.service.file.FileService;
import com.onrank.server.api.service.member.MemberService;
import com.onrank.server.api.service.study.StudyService;
import com.onrank.server.domain.file.FileCategory;
import com.onrank.server.domain.notice.Notice;
import com.onrank.server.domain.notice.NoticeJpaRepository;
import com.onrank.server.domain.study.Study;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class NoticeService {

    private final NoticeJpaRepository noticeRepository;
    private final FileService fileService;
    private final MemberService memberService;
    private final StudyService studyService;

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

    // 공지사항 생성
    @Transactional
    public ContextResponse<List<PresignedUrlResponse>> createNotice(String username, Long studyId, AddNoticeRequest request) {

        Study study = studyService.findByStudyId(studyId)
                .orElseThrow(() -> new IllegalArgumentException("Study not found"));

        // 공지 생성 및 저장
        Notice notice = request.toEntity(study);
        noticeRepository.save(notice);

        // Presigned- URL 발급 및 FileMetadata 저장
        List<PresignedUrlResponse> responses = fileService.createMultiplePresignedUrls(
                FileCategory.NOTICE, notice.getNoticeId(), request.getFileNames());

        MemberStudyContext context = memberService.getContext(username, studyId);
        return new ContextResponse<>(context, responses);
    }

    // 공지사항 수정
    @Transactional
    public ContextResponse<List<PresignedUrlResponse>> updateNotice(String username, Long studyId, Long noticeId, UpdateNoticeRequest request) {

        // 공지 엔티티 조회 및 내용 수정
        Notice notice = noticeRepository.findByNoticeId(noticeId)
                .orElseThrow(() -> new IllegalArgumentException("Notice not found"));
        notice.update(request.getNoticeTitle(), request.getNoticeContent());

        // 파일 수정
        List<PresignedUrlResponse> responses =
                fileService.replaceFiles(FileCategory.NOTICE, noticeId, request.getRemainingFileIds(), request.getNewFileNames());

        MemberStudyContext context = memberService.getContext(username, studyId);
        return new ContextResponse<>(context, responses);
    }

    // 공지사항 삭제
    @Transactional
    public MemberStudyContext deleteNotice(String username, Long studyId, Long noticeId) {
        Notice notice = noticeRepository.findByNoticeId(noticeId)
                .orElseThrow(() -> new IllegalArgumentException("Notice not found"));

        // 파일 삭제 (S3 + 메타데이터)
        fileService.deleteAllFilesAndMetadata(FileCategory.NOTICE, noticeId);
        // 공지사항 삭제
        noticeRepository.delete(notice);

        return memberService.getContext(username, studyId);
    }
}
