package com.onrank.server.common.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum CustomErrorInfo {

    // 400 BAD_REQUEST - 클라이언트의 잘못된 요청
    INVALID_REQUEST(HttpStatus.BAD_REQUEST, "Invalid request"),
    REFRESH_TOKEN_IS_NULL(HttpStatus.BAD_REQUEST, "Refresh token is null"),
    REFRESH_TOKEN_EXPIRED(HttpStatus.BAD_REQUEST, "Refresh token expired"),
    INVALID_REFRESH_TOKEN(HttpStatus.BAD_REQUEST, "Invalid refresh token"),
    REFRESH_TOKEN_ALREADY_EXISTS(HttpStatus.BAD_REQUEST, "Refresh token already exists"),
    MEMBER_NOT_FOUND(HttpStatus.BAD_REQUEST, "Member not found"),
    STUDENT_NOT_FOUND(HttpStatus.BAD_REQUEST, "Student not found"),
    STUDY_NOT_FOUND(HttpStatus.BAD_REQUEST, "Study not found"),
    POST_NOT_FOUND(HttpStatus.BAD_REQUEST, "Post not found"),
    NOTICE_NOT_FOUND(HttpStatus.BAD_REQUEST, "Notice not found"),
    ASSIGNMENT_NOT_FOUND(HttpStatus.BAD_REQUEST, "Assignment not found"),
    SUBMISSION_NOT_FOUND(HttpStatus.BAD_REQUEST, "Submission not found"),
    ATTENDANCE_NOT_FOUND(HttpStatus.BAD_REQUEST, "Attendance not found"),
    NOTIFICATION_NOT_FOUND(HttpStatus.BAD_REQUEST, "Notification not found"),
    FILE_NOT_FOUND(HttpStatus.BAD_REQUEST, "File not found"),
    SCHEDULE_NOT_FOUND(HttpStatus.BAD_REQUEST, "Schedule not found"),
    INVALID_ASSIGNMENT_DUE_DATE(HttpStatus.BAD_REQUEST, "Assignment due date is invalid"),
    INVALID_SCHEDULE_TIME(HttpStatus.BAD_REQUEST, "Invalid schedule time"),
    INVALID_ROLE_CHANGE(HttpStatus.BAD_REQUEST, "Invalid role change"),
    SUBMISSION_NOT_SUBMITTED(HttpStatus.BAD_REQUEST, "Submission not submitted yet"),


    // 401 UNAUTHORIZED - 인증 실패
    INVALID_ACCESS_TOKEN(HttpStatus.UNAUTHORIZED, "Invalid access token"),
    ACCESS_TOKEN_EXPIRED(HttpStatus.UNAUTHORIZED, "Access token expired"),
    INVALID_LOGIN_CREDENTIALS(HttpStatus.UNAUTHORIZED, "Invalid login credentials"),
    LOGIN_REQUIRED(HttpStatus.UNAUTHORIZED, "Login required"),
    OAUTH2_UNAUTHORIZED(HttpStatus.UNAUTHORIZED, "OAuth2 authorization failed"),

    // 403 FORBIDDEN - 권한 없음
    ACCESS_DENIED(HttpStatus.FORBIDDEN, "Access denied"),
    NOT_STUDY_MEMBER(HttpStatus.FORBIDDEN, "User is not a member of this study"),
    NOT_POST_WRITER(HttpStatus.FORBIDDEN, "User is not a post writer"),
    LATE_SUBMISSION(HttpStatus.FORBIDDEN, "Submission deadline has passed"),

    // 404 NOT_FOUND - 리소스를 찾을 수 없음 (현재 미사용 중)

    // 500 INTERNAL_SERVER_ERROR - 서버 내부 오류
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "Internal server error"),
    MAIL_SEND_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "Mail send error"),
    FILE_UPLOAD_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "File upload error"),
    S3_SERVICE_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "S3 service error")
    ;

    private final HttpStatus httpStatus;
    private final String message;
}
