package com.onrank.server.api.service.post;

import com.onrank.server.api.dto.file.FileMetadataDto;
import com.onrank.server.api.dto.post.AddPostRequest;
import com.onrank.server.api.dto.post.PostResponse;
import com.onrank.server.api.service.file.FileService;
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
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PostService {

    private final PostJpaRepository postRepository;
    private final StudentJpaRepository studentRepository;
    private final MemberJpaRepository memberRepository;
    private final FileService fileService;

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
    public List<FileMetadataDto> updatePost(Long postId, AddPostRequest request) {
        Post post = postRepository.findByPostId(postId)
                .orElseThrow(() -> new IllegalArgumentException("Post not found"));

        post.update(request.getPostTitle(), request.getPostContent());

        List<FileMetadata> existingFiles = fileService.findFile(FileCategory.POST, postId);
        existingFiles.forEach(file -> fileService.deleteFile(file.getFileKey()));
        fileService.deleteFileMetadata(FileCategory.POST, postId);

        fileService.createMultiplePresignedUrls(FileCategory.POST, postId, request.getFileNames());

        List<FileMetadata> files = fileService.findFile(FileCategory.POST, postId);
        return files.stream()
                .map(f -> new FileMetadataDto(f, fileService.getBucketName()))
                .collect(Collectors.toList());
    }

    // 게시판 삭제
    @Transactional
    public void deletePost(Long postId) {
        Post post = postRepository.findByPostId(postId)
                .orElseThrow(() -> new IllegalArgumentException("Post not found"));

        // S3 파일 및 메타데이터 삭제
        List<FileMetadata> files = fileService.findFile(FileCategory.POST, postId);
        files.forEach(file -> {
            fileService.deleteFile(file.getFileKey());
        });

        postRepository.delete(post);
    }

    // 게시판 상세 조회를 위한 PostResponse 객체 생성
    public PostResponse getPostResponse(Long postId) {
        Post post = postRepository.findByPostId(postId)
                .orElseThrow(() -> new IllegalArgumentException("Post not found"));

        List<FileMetadata> files = fileService.findFile(FileCategory.POST, postId);
        List<FileMetadataDto> fileDtos = files.stream()
                .map(file -> new FileMetadataDto(file, fileService.getBucketName()))
                .collect(Collectors.toList());

        return new PostResponse(post, fileDtos);
    }

    // 게시판 목록 조회를 위한 List<PostResponse> 객체 생성
    public List<PostResponse> getPostResponsesByStudyId(Long studyId) {
        return postRepository.findByStudyStudyId(studyId)
                .stream()
                .map(post -> {
                    List<FileMetadata> files = fileService.findFile(FileCategory.POST, post.getPostId());
                    List<FileMetadataDto> fileDtos = files.stream()
                            .map(file -> new FileMetadataDto(file, fileService.getBucketName()))
                            .collect(Collectors.toList());

                    return new PostResponse(post, fileDtos);
                })
                .collect(Collectors.toList());
    }
}