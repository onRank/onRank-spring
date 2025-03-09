package com.onrank.server.api.controller;

import com.onrank.server.api.dto.notice.AddNoticeRequest;
import com.onrank.server.api.dto.notice.NoticeIdResponse;
import com.onrank.server.api.dto.oauth.CustomOAuth2User;
import com.onrank.server.api.dto.post.AddPostRequest;
import com.onrank.server.api.dto.post.PostIdResponse;
import com.onrank.server.api.dto.post.PostResponse;
import com.onrank.server.api.service.member.MemberService;
import com.onrank.server.api.service.post.PostService;
import com.onrank.server.api.service.student.StudentService;
import com.onrank.server.api.service.study.StudyService;
import com.onrank.server.domain.member.Member;
import com.onrank.server.domain.notice.Notice;
import com.onrank.server.domain.post.Post;
import com.onrank.server.domain.study.Study;
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
public class PostController {

    private final PostService postService;
    private final StudentService studentService;
    private final StudyService studyService;
    private final MemberService memberService;

    /**
     * 스터디 내 모든 게시판 조회 (스터디 멤버만 가능)
     */
    @GetMapping
    public ResponseEntity<List<PostResponse>> getPosts (
            @PathVariable Long studyId,
            @AuthenticationPrincipal CustomOAuth2User oAuth2User) {

        // 스터디 멤버만 가능
        if (!memberService.isMemberInStudy(oAuth2User.getName(), studyId)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        return ResponseEntity.ok(postService.getPostResponsesByStudyId(studyId));
    }

    /**
     * 특정 게시판 조회 (스터디 멤버만 가능)
     */
    @GetMapping("/{postId}")
    public ResponseEntity<PostResponse> getPost (
            @PathVariable Long studyId,
            @PathVariable Long postId,
            @AuthenticationPrincipal CustomOAuth2User oAuth2User) {

        // 스터디 멤버만 가능
        if (!memberService.isMemberInStudy(oAuth2User.getName(), studyId)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        Post post = postService.findByPostId(postId)
                .orElseThrow(() -> new IllegalArgumentException("Post not found"));

        return ResponseEntity.ok(new PostResponse(post));
    }

    /**
     * 게시판 등록 (스터디 멤버만 가능)
     */
    @PostMapping("/add")
    public ResponseEntity<PostIdResponse> createNotice(
            @PathVariable Long studyId,
            @RequestBody AddPostRequest addPostRequest,
            @AuthenticationPrincipal CustomOAuth2User oAuth2User) {

        String username = oAuth2User.getName();

        // 스터디 멤버만 가능
        if (!memberService.isMemberInStudy(username, studyId)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        Study study = studyService.findByStudyId(studyId)
                .orElseThrow(() -> new IllegalArgumentException("Study not found"));
        Member member = memberService.findByUsernameAndStudyId(username, studyId)
                .orElseThrow(() -> new IllegalArgumentException("Member not found"));

        Post post = addPostRequest.toEntity(study, member);
        postService.createPost(post);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new PostIdResponse(post.getPostId()));
    }

    /**
     * 게시판 수정 (Post 의 작성자만 가능)
     */
    @PutMapping("/{postId}")
    public ResponseEntity<PostIdResponse> updatePost(
            @PathVariable Long studyId,
            @PathVariable Long postId,
            @RequestBody AddPostRequest addPostRequest,
            @AuthenticationPrincipal CustomOAuth2User oAuth2User) {

        // 작성자만 수정 가능
        if (postService.isMemberWriter(oAuth2User.getName(), studyId, postId)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        postService.updatePost(postId, addPostRequest.getPostTitle(),
                addPostRequest.getPostContent(), addPostRequest.getPostImagePath());

        return ResponseEntity.ok(new PostIdResponse(postId));
    }

    /**
     * 게시판 삭제 (Post 의 작성자만 가능)
     */
    @DeleteMapping("/{postId}")
    public ResponseEntity<PostIdResponse> deletePost(
            @PathVariable Long studyId,
            @PathVariable Long postId,
            @AuthenticationPrincipal CustomOAuth2User oAuth2User) {

        // 작성자만 삭제 가능
        if (postService.isMemberWriter(oAuth2User.getName(), studyId, postId)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        postService.deletePost(postId);

        return ResponseEntity.ok(new PostIdResponse(postId));
    }
}
