package com.spring.lab.security.jwt.dto;

import java.util.List;

public record BatchTransferRequest(
        List<TransferItemDto> transfers
) {
}
