package com.onrank.server.api.dto.post;

import com.onrank.server.api.dto.file.FileMetadataDto;
import com.onrank.server.domain.post.Post;
import lombok.Getter;

import java.time.LocalDate;
import java.util.List;

@Getter
public class PostResponse {

    private Long postId;
    private String postTitle;
    private String postContent;
    private LocalDate postCreatedAt;
    private LocalDate postModifiedAt;
    private String postWritenBy; // 작성자 이름
    private List<FileMetadataDto> files;

    public PostResponse(Post post, List<FileMetadataDto> files) {
        this.postId = post.getPostId();
        this.postTitle = post.getPostTitle();
        this.postContent = post.getPostContent();
        this.postCreatedAt = post.getPostCreatedAt();
        this.postModifiedAt = post.getPostModifiedAt();
        // 작성자 이름 설정
        this.postWritenBy = post.getMember().getStudent().getStudentName();
        this.files = files;
    }
}
