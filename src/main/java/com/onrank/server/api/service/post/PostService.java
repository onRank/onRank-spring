package com.onrank.server.api.service.post;

import com.onrank.server.api.dto.common.ContextResponse;
import com.onrank.server.api.dto.common.MemberStudyContext;
import com.onrank.server.api.dto.file.FileMetadataDto;
import com.onrank.server.api.dto.file.PresignedUrlResponse;
import com.onrank.server.api.dto.post.AddPostRequest;
import com.onrank.server.api.dto.post.PostDetailResponse;
import com.onrank.server.api.dto.post.PostListResponse;
import com.onrank.server.api.dto.post.UpdatePostRequest;
import com.onrank.server.api.service.file.FileService;
import com.onrank.server.api.service.member.MemberService;
import com.onrank.server.domain.file.FileCategory;
import com.onrank.server.domain.file.FileMetadata;
import com.onrank.server.domain.member.Member;
import com.onrank.server.domain.member.MemberJpaRepository;
import com.onrank.server.domain.post.Post;
import com.onrank.server.domain.post.PostJpaRepository;
import com.onrank.server.domain.student.Student;
import com.onrank.server.domain.student.StudentJpaRepository;
import com.onrank.server.domain.study.Study;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PostService {

    private final PostJpaRepository postRepository;
    private final StudentJpaRepository studentRepository;
    private final MemberJpaRepository memberRepository;
    private final MemberService memberService;
    private final FileService fileService;

    // 게시판 상세 조회
    public ContextResponse<PostDetailResponse> getPostDetail(String username, Long studyId, Long postId) {
        Post post = postRepository.findByPostId(postId)
                .orElseThrow(() -> new IllegalArgumentException("Post not found"));

        List<FileMetadataDto> fileDtos = fileService.getMultipleFileMetadata(FileCategory.POST, postId);

        MemberStudyContext context = memberService.getContext(username, studyId);
        return new ContextResponse<>(context, PostDetailResponse.from(post, fileDtos));
    }

    // 게시판 목록 조회
    public ContextResponse<List<PostListResponse>> getPosts(String username, Long studyId) {
        List<PostListResponse> responses = postRepository.findByStudyStudyId(studyId)
                .stream()
                .map(PostListResponse::from)
                .toList();

        MemberStudyContext context = memberService.getContext(username, studyId);
        return new ContextResponse<>(context, responses);
    }

    // 게시물(Post)의 작성자 인지 검증하는 메서드
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
    public List<FileMetadataDto> createPost(AddPostRequest addPostRequest, Study study, Member member) {
        Post post = addPostRequest.toEntity(study, member);
        postRepository.save(post);

        fileService.createMultiplePresignedUrls(FileCategory.POST, post.getPostId(), addPostRequest.getFileNames());

        List<FileMetadata> files = fileService.findFile(FileCategory.POST, post.getPostId());
        return files.stream()
                .map(f -> new FileMetadataDto(f, fileService.getBucketName()))
                .collect(Collectors.toList());
    }

    // 게시판 수정
    @Transactional
    public ContextResponse<List<PresignedUrlResponse>> updatePost(String username, Long StudyId, Long postId, UpdatePostRequest request) {

        // 게시판 엔티티 조회 및 내용 수정
        Post post = postRepository.findByPostId(postId)
                .orElseThrow(() -> new IllegalArgumentException("Post not found"));
        post.update(request.getPostTitle(), request.getPostContent());

        // 파일 수정
        List<PresignedUrlResponse> responses =
                fileService.replaceFiles(FileCategory.POST, postId, request.getRemainingFileIds(), request.getNewFileNames());

        MemberStudyContext context = memberService.getContext(username, StudyId);
        return new ContextResponse<>(context, responses);
    }

    // 게시판 삭제
    @Transactional
    public MemberStudyContext deletePost(String username, Long studyId, Long postId) {
        Post post = postRepository.findByPostId(postId)
                .orElseThrow(() -> new IllegalArgumentException("Post not found"));

        // 파일 삭제 (S3 + 메타데이터)
        fileService.deleteAllFilesAndMetadata(FileCategory.POST, postId);
        // 게시판 삭제
        postRepository.delete(post);

        return memberService.getContext(username, studyId);
    }
}