package org.example.is_lab1.models.dto;

import org.example.is_lab1.models.entity.ImportStatus;

import java.time.LocalDateTime;
import java.util.List;

public record ImportOperationDto(
        Long id,
        String userId,
        ImportStatus status,
        LocalDateTime startTime,
        LocalDateTime endTime,
        Integer fileCount,
        Integer addedCount,
        String errorMessage,
        List<ImportFileDto> files // детали по файлам
) {}
