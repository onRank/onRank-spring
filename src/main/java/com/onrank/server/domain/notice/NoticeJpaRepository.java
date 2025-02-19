package com.onrank.server.domain.notice;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface NoticeJpaRepository extends JpaRepository<Notice, Long> {

    List<Notice> findByStudy_Id(Long studyId);
}