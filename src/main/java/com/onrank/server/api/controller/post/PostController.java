package com.onrank.server.api.controller.post;

import com.onrank.server.api.dto.common.ContextResponse;
import com.onrank.server.api.dto.common.MemberStudyContext;
import com.onrank.server.api.dto.file.PresignedUrlResponse;
import com.onrank.server.api.dto.oauth.CustomOAuth2User;
import com.onrank.server.api.dto.post.AddPostRequest;
import com.onrank.server.api.dto.post.PostDetailResponse;
import com.onrank.server.api.dto.post.PostListResponse;
import com.onrank.server.api.dto.post.UpdatePostRequest;
import com.onrank.server.api.service.post.PostService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/studies/{studyId}/posts")
@RequiredArgsConstructor
public class PostController implements PostControllerDocs {

    private final PostService postService;

    /**
     * 게시판 목록 조회 (스터디 멤버만 가능)
     */
    @GetMapping
    public ResponseEntity<ContextResponse<List<PostListResponse>>> getPosts (
            @PathVariable Long studyId,
            @AuthenticationPrincipal CustomOAuth2User oAuth2User) {
        return ResponseEntity.ok(postService.getPosts(oAuth2User.getName(), studyId));
    }

    /**
     * 특정 게시판 조회 (스터디 멤버만 가능)
     */
    @GetMapping("/{postId}")
    public ResponseEntity<ContextResponse<PostDetailResponse>> getPost (
            @PathVariable Long studyId,
            @PathVariable Long postId,
            @AuthenticationPrincipal CustomOAuth2User oAuth2User) {
        return ResponseEntity.ok(postService.getPostDetail(oAuth2User.getName(), studyId, postId));
    }

    /**
     * 게시판 등록 (스터디 멤버만 가능)
     */
    @PostMapping("/add")
    public ResponseEntity<ContextResponse<List<PresignedUrlResponse>>> createPost(
            @PathVariable Long studyId,
            @RequestBody AddPostRequest request,
            @AuthenticationPrincipal CustomOAuth2User oAuth2User) {
        return ResponseEntity.status(HttpStatus.CREATED).body(postService.createPost(oAuth2User.getName(), studyId, request));
    }

    /**
     * 게시판 수정 (Post 의 작성자만 가능)
     */
    @PutMapping("/{postId}")
    public ResponseEntity<ContextResponse<List<PresignedUrlResponse>>> updatePost(
            @PathVariable Long studyId,
            @PathVariable Long postId,
            @RequestBody UpdatePostRequest request,
            @AuthenticationPrincipal CustomOAuth2User oAuth2User) {
        return ResponseEntity.ok(postService.updatePost(oAuth2User.getName(), studyId, postId, request));
    }

    /**
     * 게시판 삭제 (Post 의 작성자만 가능)
     */
    @DeleteMapping("/{postId}")
    public ResponseEntity<MemberStudyContext> deletePost(
            @PathVariable Long studyId,
            @PathVariable Long postId,
            @AuthenticationPrincipal CustomOAuth2User oAuth2User) {
        return ResponseEntity.ok(postService.deletePost(oAuth2User.getName(), studyId, postId));
    }
}