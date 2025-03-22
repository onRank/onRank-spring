package com.onrank.server.api.service.post;

import com.onrank.server.api.dto.file.FileMetadataDto;
import com.onrank.server.api.dto.post.AddPostRequest;
import com.onrank.server.api.dto.post.PostResponse;
import com.onrank.server.api.service.cloud.S3Service;
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
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PostService {

    private final PostJpaRepository postRepository;
    private final StudentJpaRepository studentRepository;
    private final MemberJpaRepository memberRepository;
    private final S3Service s3Service;

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
    public Map<String, Object> createPost(Post post, AddPostRequest request) {
        postRepository.save(post);

        List<Map<String, String>> presignedUrls =
                s3Service.uploadFilesWithMetadata(FileCategory.POST, post.getPostId(), request.getFileNames());

        return Map.of(
                "postId", post.getPostId(),
                "uploadUrls", presignedUrls
        );
    }

    // 게시판 상세 조회를 위한 PostResponse 객체 생성
    public PostResponse getPostResponse(Long postId) {
        Post post = postRepository.findByPostId(postId)
                .orElseThrow(() -> new IllegalArgumentException("Post not found"));

        List<FileMetadata> files = s3Service.findFile(FileCategory.POST, postId);
        List<FileMetadataDto> fileDtos = files.stream()
                .map(file -> new FileMetadataDto(file, s3Service.getBucketName()))
                .collect(Collectors.toList());

        return new PostResponse(post, fileDtos);
    }

    // 게시판 목록 조회를 위한 List<PostResponse> 객체 생성
    public List<PostResponse> getPostResponsesByStudyId(Long studyId) {
        return postRepository.findByStudyStudyId(studyId)
                .stream()
                .map(post -> {
                    List<FileMetadata> files = s3Service.findFile(FileCategory.POST, post.getPostId());
                    List<FileMetadataDto> fileDtos = files.stream()
                            .map(file -> new FileMetadataDto(file, s3Service.getBucketName()))
                            .collect(Collectors.toList());

                    return new PostResponse(post, fileDtos);
                })
                .collect(Collectors.toList());
    }

    // 게시판 수정
    @Transactional
    public Map<String, Object> updatePost(Long postId, String postTitle, String postContent, List<String> newFileNames) {
        Post post = postRepository.findByPostId(postId)
                .orElseThrow(() -> new IllegalArgumentException("해당 게시판이 존재하지 않습니다."));

        // 기존 파일 모두 삭제
        List<FileMetadata> existingFiles = s3Service.findFile(FileCategory.POST, postId);
        existingFiles.forEach(file -> {
            s3Service.deleteFile(file.getFilePath());
        });

        // 메타데이터도 삭제
        s3Service.deleteFileMetadata(FileCategory.POST, postId); // 🔥 해당 메서드 추가 필요

        // 새 파일 업로드
        List<Map<String, String>> uploadUrls = s3Service.uploadFilesWithMetadata(
                FileCategory.POST, postId, newFileNames
        );

        return Map.of(
                "postId", postId,
                "uploadUrls", uploadUrls
        );

    }

    // 게시판 삭제
    @Transactional
    public void deletePost(Long postId) {
        Post post = postRepository.findByPostId(postId)
                .orElseThrow(() -> new IllegalArgumentException("Post not found"));

        // S3 파일 및 메타데이터 삭제
        List<FileMetadata> files = s3Service.findFile(FileCategory.POST, postId);
        files.forEach(file -> {
            s3Service.deleteFile(file.getFilePath());
        });

        postRepository.delete(post);
    }
}