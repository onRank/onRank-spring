package com.onrank.server.domain.study;

import com.onrank.server.domain.member.Member;
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
    private Long id;

    @Column(nullable = false)
    private String name; // 스터디 이름

    @Column(nullable = false)
    private String content; // 스터디 설명

    // Study와 Member의 1:N 관계 설정
    @OneToMany(mappedBy = "study", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Member> members = new ArrayList<>();

    // 생성자
    @Builder
    public Study(String name, String description) {
        this.name = name;
        this.content = description;
    }
}
