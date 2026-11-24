package com.spring.lab.security.jwt.exception;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.spring.lab.security.jwt.dto.SecurityErrorResponse;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.LocalDateTime;

@Component
public class BankAuthenticationEntryPoint implements AuthenticationEntryPoint {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException)
            throws IOException, ServletException {
        response.setStatus(HttpStatus.UNAUTHORIZED.value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setHeader("WWW-Authenticate", "Bearer error=\"invalid_token\"");

        SecurityErrorResponse errorResponse = new SecurityErrorResponse(
                java.time.Instant.now().toString(),
                HttpStatus.UNAUTHORIZED.value(),
                "Unauthorized Access",
                authException.getMessage() != null ? authException.getMessage() : "Authentication token missing, invalid, or expired",
                request.getRequestURI()
        );

        response.getWriter().write(objectMapper.writeValueAsString(errorResponse));
    }
}
