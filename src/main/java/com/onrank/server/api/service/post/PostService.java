package com.onrank.server.api.service.post;

import com.onrank.server.api.dto.notice.NoticeResponse;
import com.onrank.server.api.dto.post.PostResponse;
import com.onrank.server.api.service.student.StudentService;
import com.onrank.server.domain.member.Member;
import com.onrank.server.domain.member.MemberJpaRepository;
import com.onrank.server.domain.post.Post;
import com.onrank.server.domain.post.PostJpaRepository;
import com.onrank.server.domain.student.Student;
import com.onrank.server.domain.student.StudentJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PostService {

    private final PostJpaRepository postRepository;
    private final StudentJpaRepository studentRepository;
    private final MemberJpaRepository memberRepository;

    public Optional<Post> findByPostId(Long postId) {
        return postRepository.findByPostId(postId);
    }

    public List<Post> findByStudyId(Long studyId) {
        return postRepository.findByStudyStudyId(studyId);
    }

    /**
     * 게시물(Post)의 작성자 인지 검증하는 메서드
     */
    public boolean isMemberWriter(String username, Long studyId, Long postId) {
        Student student = studentRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("Student not found"));
        Long studentId = student.getStudentId();

        Post post = postRepository.findByPostId(postId)
                .orElseThrow(() -> new IllegalArgumentException("Post not found"));
        Member postWriter = post.getMember();

        Member loggedInMember = memberRepository.findByStudentStudentIdAndStudyStudyId(studentId, studyId)
                .orElseThrow(() -> new IllegalArgumentException("Member not found"));

        return postWriter.equals(loggedInMember);
    }

    @Transactional
    public void createPost(Post post) {
        postRepository.save(post);
    }

    // 게시판 수정
    @Transactional
    public void updatePost(Long postId, String postTitle, String postContent, String postImagePath) {
        Post post = postRepository.findByPostId(postId)
                .orElseThrow(() -> new IllegalArgumentException("해당 게시판이 존재하지 않습니다."));

        post.update(postTitle, postContent, postImagePath);
    }

    // 게시판 삭제
    @Transactional
    public void deletePost(Long postId) {
        Post post = postRepository.findByPostId(postId)
                .orElseThrow(() -> new IllegalArgumentException("Post not found"));

        postRepository.delete(post);
    }

    // 게시판 목록 조회를 위한 List<PostResponse> 객체 생성
    public List<PostResponse> getPostResponsesByStudyId(Long studyId) {
        return postRepository.findByStudyStudyId(studyId)
                .stream()
                .map(PostResponse::new)
                .collect(Collectors.toList());
    }
}

