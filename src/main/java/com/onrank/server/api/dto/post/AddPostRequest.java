package com.onrank.server.api.dto.post;

import com.onrank.server.domain.member.Member;
import com.onrank.server.domain.post.Post;
import com.onrank.server.domain.study.Study;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Getter
@NoArgsConstructor
public class AddPostRequest {

    @NotBlank
    private String postTitle;

    @NotBlank
    private String postContent;

    // 업로드하기 위한 파일명들
    private List<String> fileNames;

    public Post toEntity(Study study, Member member) {
        return Post.builder()
                .postTitle(postTitle)
                .postContent(postContent)
                .postCreatedAt(LocalDate.now())
                .postModifiedAt(LocalDate.now())
                .study(study)
                .member(member)
                .build();
    }
}