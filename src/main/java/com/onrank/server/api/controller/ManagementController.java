package com.onrank.server.api.controller;

import com.onrank.server.api.dto.error.ErrorResponse;
import com.onrank.server.api.dto.member.AddMemberRequestDto;
import com.onrank.server.api.dto.member.AddMemberResponseDto;
import com.onrank.server.api.dto.member.UpdateMemberRoleRequestDto;
import com.onrank.server.api.dto.oauth.CustomOAuth2User;
import com.onrank.server.api.exception.ForbiddenException;
import com.onrank.server.api.exception.NotFoundException;
import com.onrank.server.api.service.member.MemberService;
import com.onrank.server.domain.member.Member;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.headers.Header;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("studies/{studyId}/management")
@RequiredArgsConstructor
public class ManagementController {

    private final MemberService memberService;

    @Operation(summary = "스터디 멤버 조회", description = "스터디에 속한 모든 멤버 목록을 조회합니다.")
    @ApiResponse(
            responseCode = "200",
            description = "조회 성공",
            content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = AddMemberResponseDto.class)
            )
    )
    @GetMapping("/members")
    public ResponseEntity<List<AddMemberResponseDto>> getStudyMembers(@PathVariable Long studyId) {
        List<AddMemberResponseDto> members = memberService.getStudyMembers(studyId);
        log.info("members: {}", members);
        return ResponseEntity.ok(members);
    }

    @Operation(
            summary = "스터디 멤버 추가",
            description = "스터디에 새로운 멤버를 추가합니다.",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "추가할 멤버 정보",
                    required = true,
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = AddMemberRequestDto.class)
                    )
            )
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "201",
                    description = "멤버 추가 성공",
                    headers = {
                            @Header(
                                    name = "Location",
                                    description = "생성된 멤버 리소스의 URI",
                                    schema = @Schema(type = "string", example = "/studies/1/management/members/42")
                            )
                    }
            ),
            @ApiResponse(responseCode = "404", description = "스터디를 찾을 수 없음",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class)
                    )),
            @ApiResponse(responseCode = "400", description = "잘못된 요청",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class)
                    ))
    })
    @PostMapping("/members")
    public ResponseEntity<Void> addMember(
            @PathVariable Long studyId,
            @RequestBody @Valid AddMemberRequestDto requestDto) {

        Member member = memberService.addMemberToStudy(studyId, requestDto);
        if (member == null) {
            throw new NotFoundException("스터디 또는 멤버를 찾을 수 없습니다.");
        }

        URI memberURI = URI.create("/studies/" + studyId + "/management/members/" + member.getMemberId());

        return ResponseEntity.created(memberURI).build();
    }

    @Operation(
            summary = "스터디 멤버 역할 변경",
            description = "스터디 내 멤버의 역할을 변경합니다.",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "변경할 역할 정보",
                    required = true,
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = UpdateMemberRoleRequestDto.class)
                    )
            )
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "역할 변경 성공"),
            @ApiResponse(responseCode = "403", description = "접근 권한 없음",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class)
                    ))
    })
    @PutMapping("/members/{memberId}/role")
    public ResponseEntity<Void> updateMemberRole(
            @PathVariable Long studyId,
            @PathVariable Long memberId,
            @RequestBody @Valid UpdateMemberRoleRequestDto requestDto,
            @AuthenticationPrincipal CustomOAuth2User oAuth2User) {

        validateStudyAccess(studyId, memberId, oAuth2User);

        memberService.UpdateMemberRole(memberId, requestDto.getMemberRole());

        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "스터디 멤버 삭제", description = "스터디에서 특정 멤버를 삭제합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "멤버 삭제 성공"),
            @ApiResponse(responseCode = "403", description = "접근 권한 없음",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class)
                    ))
    })
    @DeleteMapping("/members/{memberId}")
    public ResponseEntity<Void> deleteMember(
            @PathVariable Long studyId,
            @PathVariable Long memberId,
            @AuthenticationPrincipal CustomOAuth2User oAuth2User) {

        validateStudyAccess(studyId, memberId, oAuth2User);

        memberService.deleteMember(memberId);

        return ResponseEntity.noContent().build();
    }

    private void validateStudyAccess(Long studyId, Long memberId, CustomOAuth2User oAuth2User) {
        String username = oAuth2User.getName();

        if (!memberService.isMemberInStudy(username, studyId)) {
            throw new ForbiddenException("해당 스터디에 대한 접근 권한이 없습니다.");
        }

        if (!memberService.isMemberHost(username, studyId)) {
            throw new ForbiddenException("호스트 권한이 필요합니다.");
        }

        if (!memberService.isMemberInStudy(memberId, studyId)) {
            throw new ForbiddenException("해당 멤버는 스터디에 속해있지 않습니다.");
        }
    }
}
