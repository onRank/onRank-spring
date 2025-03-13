package com.onrank.server.api.dto.post;

import com.onrank.server.domain.member.Member;
import com.onrank.server.domain.post.Post;
import com.onrank.server.domain.study.Study;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Getter
@NoArgsConstructor
public class AddPostRequest {

    @NotBlank
    @Size(max = 255)
    private String postTitle;

    @NotBlank
    private String postContent;

    private String postImagePath;

    public Post toEntity(Study study, Member member) {
        return Post.builder()
                .postTitle(postTitle)
                .postContent(postContent)
                .postCreatedAt(LocalDate.now())
                .postModifiedAt(LocalDate.now())
                .postImagePath(postImagePath)
                .study(study)
                .member(member)
                .build();
    }
}