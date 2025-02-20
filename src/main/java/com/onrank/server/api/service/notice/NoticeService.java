package com.onrank.server.api.service.notice;

import com.onrank.server.api.dto.notice.NoticeResponse;
import com.onrank.server.domain.notice.Notice;
import com.onrank.server.domain.notice.NoticeJpaRepository;
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

    public Optional<Notice> findByNoticeId(Long noticeId) {
        return noticeRepository.findByNoticeId(noticeId);
    }

    public List<Notice> findByStudyId(Long studyId) {
        return noticeRepository.findByStudyStudyId(studyId);
    }

    @Transactional
    public void createNotice(Notice notice) {
        noticeRepository.save(notice);
    }

    // 공지사항 수정
    @Transactional
    public void updateNotice(Long noticeId, String noticeTitle, String noticeContent, String noticeImagePath) {
        Notice notice = noticeRepository.findByNoticeId(noticeId)
                .orElseThrow(() -> new IllegalArgumentException("해당 공지사항이 존재하지 않습니다."));

        notice.update(noticeTitle, noticeContent, noticeImagePath);
    }

    // 공지사항 삭제
    @Transactional
    public void deleteNotice(Long noticeId) {
        Notice notice = noticeRepository.findByNoticeId(noticeId)
                .orElseThrow(() -> new IllegalArgumentException("해당 공지사항이 존재하지 않습니다."));

        noticeRepository.delete(notice);
    }

    // 공지사항 목록 조회를 위한 List<noticeResponse> 객체 생성
    public List<NoticeResponse> getNoticeResponsesByStudyId(Long studyId) {
        return noticeRepository.findByStudyStudyId(studyId)
                .stream()
                .map(NoticeResponse::new)
                .collect(Collectors.toList());
    }
}
