package com.spring.lab.security.jwt.dto;

public record BankUserRegistrationRequest(
        String name,
        String email,
        String password,
        String mobileNumber,
        String role
) {
}
