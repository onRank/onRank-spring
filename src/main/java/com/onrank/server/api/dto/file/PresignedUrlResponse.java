package com.onrank.server.api.dto.file;

public record PresignedUrlResponse(String fileName, String uploadUrl) {
}
