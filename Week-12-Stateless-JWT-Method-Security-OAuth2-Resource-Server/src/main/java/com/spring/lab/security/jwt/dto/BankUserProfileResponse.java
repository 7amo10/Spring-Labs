package com.spring.lab.security.jwt.dto;

import java.time.LocalDate;
import java.util.List;

public record BankUserProfileResponse(
        Long id,
        String name,
        String email,
        String mobileNumber,
        String role,
        LocalDate createDt,
        List<String> authorities
) {
}
