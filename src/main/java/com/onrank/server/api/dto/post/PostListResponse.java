package com.onrank.server.api.dto.post;

import com.onrank.server.domain.post.Post;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDate;

@Schema(description = "공지사항 목록 조회용 응답 DTO")
public record PostListResponse (

        @Schema(description = "게시판 ID", example = "1")
        Long postId,

        @Schema(description = "게시판 제목", example = "1주차 공지사항")
        String postTitle,

        @Schema(description = "게시판 내용", example = "1주차 공지사항 내용입니다.")
        String postContent,

        @Schema(description = "게시판 생성 시간", example = "2025-04-01")
        LocalDate postCreatedAt,

        @Schema(description = "게시판 최종 수정 시간", example = "2025-04-03")
        LocalDate postModifiedAt,

        @Schema(description = "게시판 작성자 이름", example = "곽민서")
        String postWritenBy
) {
    public static PostListResponse from (Post post) {
        return new PostListResponse(
                post.getPostId(),
                post.getPostTitle(),
                post.getPostContent(),
                post.getPostCreatedAt(),
                post.getPostModifiedAt(),
                post.getMember().getStudent().getStudentName()
        );
    }
}
