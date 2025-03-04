package com.onrank.server.domain.file;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "files")
public class File {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long fileId;

    @Column(nullable = false, unique = true)
    private String fileName;

    @Column(nullable = false)
    private LocalDate fileCreatedAt;

    @Column(nullable = false)
    private LocalDate fileModifiedAt;

    @Builder
    public File(String fileName, LocalDate fileCreatedAt, LocalDate fileModifiedAt) {
        this.fileName = fileName;
        this.fileCreatedAt = fileCreatedAt;
        this.fileModifiedAt = fileModifiedAt;
    }
}
