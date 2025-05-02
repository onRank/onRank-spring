package com.onrank.server.api.controller.post;

import com.onrank.server.api.dto.common.ContextResponse;
import com.onrank.server.api.dto.common.MemberStudyContext;
import com.onrank.server.api.dto.file.FileMetadataDto;
import com.onrank.server.api.dto.file.PresignedUrlResponse;
import com.onrank.server.api.dto.oauth.CustomOAuth2User;
import com.onrank.server.api.dto.post.AddPostRequest;
import com.onrank.server.api.dto.post.PostDetailResponse;
import com.onrank.server.api.dto.post.PostListResponse;
import com.onrank.server.api.dto.post.UpdatePostRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "게시판 API", description = "스터디 게시판 CRUD 기능 제공 (작성자만 수정·삭제 가능)")
public interface PostControllerDocs {

    @Operation(summary = "게시글 목록 조회", description = "스터디 멤버만 해당 스터디의 게시글 목록을 조회할 수 있습니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "게시글 목록 조회 성공"),
            @ApiResponse(responseCode = "403", description = "스터디 멤버가 아닌 경우 접근 불가")
    })
    @GetMapping
    ResponseEntity<ContextResponse<List<PostListResponse>>> getPosts(
            @Parameter(description = "스터디 ID", example = "1") @PathVariable Long studyId,
            @Parameter(hidden = true) @AuthenticationPrincipal CustomOAuth2User oAuth2User
    );

    @Operation(summary = "게시글 상세 조회", description = "스터디 멤버만 특정 게시글의 상세 정보를 조회할 수 있습니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "게시글 상세 조회 성공"),
            @ApiResponse(responseCode = "403", description = "스터디 멤버가 아닌 경우 접근 불가"),
            @ApiResponse(responseCode = "404", description = "해당 ID의 게시글이 존재하지 않음")
    })
    @GetMapping("/{postId}")
    ResponseEntity<ContextResponse<PostDetailResponse>> getPost(
            @Parameter(description = "스터디 ID", example = "1") @PathVariable Long studyId,
            @Parameter(description = "게시글 ID", example = "10") @PathVariable Long postId,
            @Parameter(hidden = true) @AuthenticationPrincipal CustomOAuth2User oAuth2User
    );

    @Operation(summary = "게시글 등록", description = "스터디 멤버만 게시글을 등록할 수 있습니다.\n\n" +
            "- 등록 시 첨부할 파일 이름 목록을 함께 전달하면, 서버는 해당 파일에 대한 presigned URL을 반환합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "게시글 등록 성공"),
            @ApiResponse(responseCode = "403", description = "스터디 멤버가 아닌 경우 접근 불가"),
            @ApiResponse(responseCode = "400", description = "요청 데이터가 유효하지 않음")
    })
    @PostMapping("/add")
    ResponseEntity<ContextResponse<List<PresignedUrlResponse>>> createPost(
            @Parameter(description = "스터디 ID", example = "1") @PathVariable Long studyId,
            @Parameter(description = "게시글 등록 요청 DTO", required = true) @RequestBody AddPostRequest addPostRequest,
            @Parameter(hidden = true) @AuthenticationPrincipal CustomOAuth2User oAuth2User
    );

    @Operation(summary = "게시글 수정", description = "게시글 작성자만 해당 게시글을 수정할 수 있습니다.\n\n" +
            "- 수정 시 유지할 파일 ID 목록과 새 파일 이름 목록을 전달합니다.\n" +
            "- 서버는 유지할 파일을 제외한 나머지를 삭제하고, 새 파일에 대한 presigned URL을 반환합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "게시글 수정 성공"),
            @ApiResponse(responseCode = "403", description = "작성자가 아닌 경우 수정 불가"),
            @ApiResponse(responseCode = "404", description = "해당 ID의 게시글이 존재하지 않음")
    })
    @PutMapping("/{postId}")
    ResponseEntity<ContextResponse<List<PresignedUrlResponse>>> updatePost(
            @Parameter(description = "스터디 ID", example = "1") @PathVariable Long studyId,
            @Parameter(description = "게시글 ID", example = "10") @PathVariable Long postId,
            @Parameter(description = "게시글 수정 요청 DTO", required = true) @RequestBody UpdatePostRequest request,
            @Parameter(hidden = true) @AuthenticationPrincipal CustomOAuth2User oAuth2User
    );

    @Operation(summary = "게시글 삭제", description = "게시글 작성자만 해당 게시글을 삭제할 수 있습니다.\n\n" +
            "- 게시글에 첨부된 파일들도 함께 S3에서 삭제됩니다.\n" +
            "- 삭제 후 스터디 내 사용자 컨텍스트 정보(MemberStudyContext)를 반환합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "게시글 삭제 성공"),
            @ApiResponse(responseCode = "403", description = "작성자가 아닌 경우 삭제 불가"),
            @ApiResponse(responseCode = "404", description = "해당 ID의 게시글이 존재하지 않음")
    })
    @DeleteMapping("/{postId}")
    ResponseEntity<MemberStudyContext> deletePost(
            @Parameter(description = "스터디 ID", example = "1") @PathVariable Long studyId,
            @Parameter(description = "게시글 ID", example = "10") @PathVariable Long postId,
            @Parameter(hidden = true) @AuthenticationPrincipal CustomOAuth2User oAuth2User
    );
}
