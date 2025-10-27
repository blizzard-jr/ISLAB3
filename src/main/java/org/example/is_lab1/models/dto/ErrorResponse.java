package org.example.is_lab1.models.dto;

import java.time.LocalDateTime;

public record ErrorResponse(
        int status,
        String error,
        String message) {}
