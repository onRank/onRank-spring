package com.onrank.server.domain.study;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface StudyJpaRepository extends JpaRepository<Study, Long> {

    Optional<Study> findByStudyId(Long id);

    // Study -> Member -> Student.username으로 조회
    @Query("SELECT s FROM Study s JOIN s.members m WHERE m.student.username = :username")
    List<Study> findAllByStudentUsername(@Param("username") String username);
}
