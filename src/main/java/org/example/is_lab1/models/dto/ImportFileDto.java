package org.example.is_lab1.models.dto;

import org.example.is_lab1.models.entity.ImportStatus;

public record ImportFileDto(
        Long id,
        String fileName,
        ImportStatus status,
        Integer addedCount,
        String errorMessage
) {}
