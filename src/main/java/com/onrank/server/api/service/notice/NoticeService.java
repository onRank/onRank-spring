package com.onrank.server.api.service.notice;

import com.onrank.server.domain.notice.Notice;
import com.onrank.server.domain.notice.NoticeJpaRepository;
import com.onrank.server.domain.student.Student;
import com.onrank.server.domain.student.StudentJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class NoticeService {

    private final NoticeJpaRepository noticeRepository;

    public Optional<Notice> findById(Long id) {
        return noticeRepository.findById(id);
    }

    public List<Notice> findAll() {
        return noticeRepository.findAll();
    }

    public List<Notice> findByStudyId(Long studyId) {
        return noticeRepository.findByStudy_Id(studyId);
    }

    @Transactional
    public void createNotice(Notice notice) {
        noticeRepository.save(notice);
    }

    /**
     * 공지사항 수정 메서드
     */
    @Transactional
    public void updateNotice(Long noticeId, String title, String content, String imagePath) {
        Notice notice = noticeRepository.findById(noticeId)
                .orElseThrow(() -> new IllegalArgumentException("해당 공지사항이 존재하지 않습니다."));

        notice.update(title, content, imagePath);
    }

    /**
     * 공지사항 삭제 메서드
     */
    @Transactional
    public void deleteNotice(Long noticeId) {
        Notice notice = noticeRepository.findById(noticeId)
                .orElseThrow(() -> new IllegalArgumentException("해당 공지사항이 존재하지 않습니다."));

        noticeRepository.delete(notice);
    }
}
