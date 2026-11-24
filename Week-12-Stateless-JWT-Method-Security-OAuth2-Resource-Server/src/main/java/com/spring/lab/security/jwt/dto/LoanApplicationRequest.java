package com.spring.lab.security.jwt.dto;

public record LoanApplicationRequest(
        String customerEmail,
        String loanType,
        Double loanAmount
) {
}
