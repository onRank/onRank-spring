package com.onrank.server.domain.file;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface FileMetadataJpaRepository extends JpaRepository<FileMetadata, Long> {

    List<FileMetadata> findByCategoryAndEntityId(FileCategory category, Long entityId);
}
