package com.onrank.server.api.dto.post;

import com.onrank.server.domain.post.Post;
import lombok.Getter;

import java.time.LocalDate;

@Getter
public class PostResponse {

    private Long postId;
    private String postTitle;
    private String postContent;
    private String postImagePath;
    private LocalDate postCreatedAt;
    private LocalDate postModifiedAt;
    private String postWritenBy; // 작성자 이름

    public PostResponse(Post post) {
        this.postId = post.getPostId();
        this.postTitle = post.getPostTitle();
        this.postContent = post.getPostContent();
        this.postImagePath = post.getPostImagePath();
        this.postCreatedAt = post.getPostCreatedAt();
        this.postModifiedAt = post.getPostModifiedAt();
        // 작성자 이름 설정
        this.postWritenBy = post.getMember().getStudent().getStudentName();
    }
}
