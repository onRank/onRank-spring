package com.onrank.server.domain.student;

import jakarta.persistence.*;
import lombok.*;

//import java.util.ArrayList;
//import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "users")
public class UserEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "student_id")
    private Long id;

    private String name;

    private String username;

    private String email;

    private String role;


//    @OneToMany(mappedBy = "student", cascade = CascadeType.ALL, orphanRemoval = true)
//    private List<Member> members = new ArrayList<>();

    // 생성자
    @Builder
    public UserEntity(String name, String username, String email, String role) {
        this.name = name;
        this.username = username;
        this.email = email;
        this.role = role;
    }
}