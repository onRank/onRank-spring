package com.onrank.server.domain.post;

import com.onrank.server.domain.member.Member;
import com.onrank.server.domain.study.Study;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "posts")
public class Post {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long postId;

    @Column(nullable = false)
    private String postTitle;

    @Column(nullable = false)
    private String postContent;

    @Column(nullable = false)
    private LocalDate postCreatedAt;

    @Column(nullable = false)
    private LocalDate postModifiedAt;

    private String postImagePath;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "study_id")
    private Study study;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @Builder
    public Post(Long postId, String postTitle, String postContent, LocalDate postCreatedAt, LocalDate postModifiedAt, String postImagePath, Study study, Member member) {
        this.postId = postId;
        this.postTitle = postTitle;
        this.postContent = postContent;
        this.postCreatedAt = postCreatedAt;
        this.postModifiedAt = postModifiedAt;
        this.postImagePath = postImagePath;
        this.study = study;
        this.member = member;
    }


    /**
     * 게시판 수정 메서드
     */
    public void update(String postTitle, String postContent, String postImagePath) {
        this.postTitle = postTitle;
        this.postContent = postContent;
        this.postImagePath = postImagePath;
        this.postModifiedAt = LocalDate.now();
    }
}
