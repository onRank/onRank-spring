package com.onrank.server.domain.study;

import com.onrank.server.domain.member.Member;
import com.onrank.server.domain.notice.Notice;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "studies")
public class Study {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "study_id")
    private Long studyId;

    @Column(name = "study_name", nullable = false)
    private String studyName; // 스터디 이름

    @Column(name = "study_content", nullable = false)
    private String studyContent; // 스터디 설명

    @Column(name = "study_image_url", columnDefinition = "TEXT")
    private String studyImageUrl; // 스터디 이미지

    @Column(name = "study_google_form_url")
    private String studyGoogleFormUrl; // 스터디 구글폼 url

    // Study와 Member의 1:N 관계 설정
    @OneToMany(mappedBy = "study", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Member> members = new ArrayList<>();

    // Study와 Notice의 1:N 관계 설정
    @OneToMany(mappedBy = "study", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Notice> notices = new ArrayList<>();

    // 생성자
    @Builder
    public Study(String studyName, String studyContent, String studyImageUrl, String studyGoogleFormUrl) {
        this.studyName = studyName;
        this.studyContent = studyContent;
        this.studyImageUrl = studyImageUrl;
        this.studyGoogleFormUrl = studyGoogleFormUrl;
    }
}