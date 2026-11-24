package com.spring.lab.security.jwt.dto;

public record TransferItemDto(
        Long accountNumber,
        String senderEmail,
        String recipientEmail,
        Double amount,
        String description
) {
}
