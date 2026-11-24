package com.spring.lab.security.jwt.dto;

import java.time.LocalDateTime;

public record SecurityErrorResponse(
        String timestamp,
        int status,
        String error,
        String message,
        String path
) {
}
