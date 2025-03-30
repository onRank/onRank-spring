package com.onrank.server.api.controller.post;

import com.onrank.server.api.dto.oauth.CustomOAuth2User;
import com.onrank.server.api.dto.post.AddPostRequest;
import com.onrank.server.api.dto.post.PostResponse;
import com.onrank.server.api.service.member.MemberService;
import com.onrank.server.api.service.post.PostService;
import com.onrank.server.api.service.study.StudyService;
import com.onrank.server.domain.member.Member;
import com.onrank.server.domain.study.Study;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/studies/{studyId}/posts")
@RequiredArgsConstructor
public class PostController {

    private final PostService postService;
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

        return ResponseEntity.ok(postService.getPostResponse(postId));
    }

    /**
     * 게시판 등록 (스터디 멤버만 가능)
     */
    @PostMapping("/add")
    public ResponseEntity<Map<String, Object>> createPost(
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

        // Pre-signed URL 생성 및 파일 메타데이터 저장
        Map<String, Object> result = postService.createPost(addPostRequest, study, member);

        return ResponseEntity.status(HttpStatus.CREATED).body(result);
    }

    /**
     * 게시판 수정 (Post 의 작성자만 가능)
     */
    @PutMapping("/{postId}")
    public ResponseEntity<Map<String, Object>> updatePost(
            @PathVariable Long studyId,
            @PathVariable Long postId,
            @RequestBody AddPostRequest addPostRequest,
            @AuthenticationPrincipal CustomOAuth2User oAuth2User) {


        // 작성자만 수정 가능
        if (!postService.isMemberWriter(oAuth2User.getName(), studyId, postId)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        Map<String, Object> result = postService.updatePost(
                postId,
                addPostRequest.getPostTitle(),
                addPostRequest.getPostContent(),
                addPostRequest.getFileNames()
        );
        return ResponseEntity.ok(result);
    }

    /**
     * 게시판 삭제 (Post 의 작성자만 가능)
     */
    @DeleteMapping("/{postId}")
    public ResponseEntity<Void> deletePost(
            @PathVariable Long studyId,
            @PathVariable Long postId,
            @AuthenticationPrincipal CustomOAuth2User oAuth2User) {

        // 작성자만 삭제 가능
        if (!postService.isMemberWriter(oAuth2User.getName(), studyId, postId)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        postService.deletePost(postId);

        return ResponseEntity.noContent().build();
    }
}