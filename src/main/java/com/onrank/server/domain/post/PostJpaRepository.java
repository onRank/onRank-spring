package com.onrank.server.domain.post;

import com.onrank.server.domain.member.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PostJpaRepository extends JpaRepository<Post, Long> {

    Optional<Post> findByPostId(Long postId);

    List<Post> findByStudyStudyId(Long studyId);
}
