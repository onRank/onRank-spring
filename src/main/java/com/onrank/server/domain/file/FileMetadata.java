package com.onrank.server.domain.file;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "file_metadata")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class FileMetadata {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long fileId;

    @Enumerated(EnumType.STRING)
    private FileCategory category; // POST, NOTICE, STUDY, ASSIGNMENT

    private Long entityId; // 특정 Post, Notice, Study, Assignment 의 Id

    private String fileName;

    private String fileKey; // S3 저장경로 (uniqueFileName)
}
