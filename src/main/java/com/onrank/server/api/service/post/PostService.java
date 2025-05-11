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
import com.onrank.server.api.service.notification.NotificationService;
import com.onrank.server.api.service.study.StudyService;
import com.onrank.server.common.exception.CustomException;
import com.onrank.server.domain.file.FileCategory;
import com.onrank.server.domain.member.Member;
import com.onrank.server.domain.member.MemberJpaRepository;
import com.onrank.server.domain.notification.NotificationCategory;
import com.onrank.server.domain.post.Post;
import com.onrank.server.domain.post.PostJpaRepository;
import com.onrank.server.domain.student.Student;
import com.onrank.server.domain.student.StudentJpaRepository;
import com.onrank.server.domain.study.Study;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static com.onrank.server.common.exception.CustomErrorInfo.*;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PostService {

    private final PostJpaRepository postRepository;
    private final StudentJpaRepository studentRepository;
    private final MemberJpaRepository memberRepository;
    private final MemberService memberService;
    private final FileService fileService;
    private final StudyService studyService;
    private final NotificationService notificationService;

    // 게시판 상세 조회
    public ContextResponse<PostDetailResponse> getPostDetail(String username, Long studyId, Long postId) {

        // 스터디 멤버만 가능
        if (!memberService.isMemberInStudy(username, studyId)) {
            throw new CustomException(NOT_STUDY_MEMBER);
        }

        Post post = postRepository.findByPostId(postId)
                .orElseThrow(() -> new IllegalArgumentException("Post not found"));

        List<FileMetadataDto> fileDtos = fileService.getMultipleFileMetadata(FileCategory.POST, postId);

        MemberStudyContext context = memberService.getContext(username, studyId);
        return new ContextResponse<>(context, PostDetailResponse.from(post, fileDtos));
    }

    // 게시판 목록 조회
    public ContextResponse<List<PostListResponse>> getPosts(String username, Long studyId) {

        // 스터디 멤버만 가능
        if (!memberService.isMemberInStudy(username, studyId)) {
            throw new CustomException(NOT_STUDY_MEMBER);
        }

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
    public ContextResponse<List<PresignedUrlResponse>> createPost(String username, Long studyId, AddPostRequest request) {

        // 스터디 멤버만 가능
        if (!memberService.isMemberInStudy(username, studyId)) {
            throw new CustomException(NOT_STUDY_MEMBER);
        }

        Study study = studyService.findByStudyId(studyId)
                .orElseThrow(() -> new CustomException(STUDY_NOT_FOUND));
        Member member = memberService.findMemberByUsernameAndStudyId(username, studyId)
                .orElseThrow(() -> new CustomException(MEMBER_NOT_FOUND));

        // 게시판 생성 및 저장
        Post post = request.toEntity(study, member);
        postRepository.save(post);

        // Presigned- URL 발급 및 FileMetadata 저장
        List<PresignedUrlResponse> responses = fileService.createMultiplePresignedUrls(
                FileCategory.POST, post.getPostId(), request.getFileNames());

        // 알림 생성
        notificationService.createNotification(NotificationCategory.POST, post.getPostId(), studyId, post.getPostTitle(), post.getPostContent(),
                "/studies/" + studyId + "/posts/" + post.getPostId(), member.getStudent());

        MemberStudyContext context = memberService.getContext(username, studyId);
        return new ContextResponse<>(context, responses);
    }

    // 게시판 수정
    @Transactional
    public ContextResponse<List<PresignedUrlResponse>> updatePost(String username, Long studyId, Long postId, UpdatePostRequest request) {


        // 작성자만 삭제 가능
        if (!isMemberWriter(username, studyId, postId)) {
            throw new CustomException(NOT_STUDY_MEMBER);
        }

        // 게시판 엔티티 조회 및 내용 수정
        Post post = postRepository.findByPostId(postId)
                .orElseThrow(() -> new IllegalArgumentException("Post not found"));
        post.update(request.getPostTitle(), request.getPostContent());

        // 파일 수정
        List<PresignedUrlResponse> responses =
                fileService.replaceFiles(FileCategory.POST, postId, request.getRemainingFileIds(), request.getNewFileNames());

        MemberStudyContext context = memberService.getContext(username, studyId);
        return new ContextResponse<>(context, responses);
    }

    // 게시판 삭제
    @Transactional
    public MemberStudyContext deletePost(String username, Long studyId, Long postId) {

        // 작성자만 삭제 가능
        if (!isMemberWriter(username, studyId, postId)) {
            throw new CustomException(NOT_STUDY_MEMBER);
        }

        Post post = postRepository.findByPostId(postId)
                .orElseThrow(() -> new IllegalArgumentException("Post not found"));

        // 알림 삭제
        notificationService.deleteNotification(NotificationCategory.POST, postId);
        // 파일 삭제 (S3 + 메타데이터)
        fileService.deleteAllFilesAndMetadata(FileCategory.POST, postId);
        // 게시판 삭제
        postRepository.delete(post);

        return memberService.getContext(username, studyId);
    }
}

