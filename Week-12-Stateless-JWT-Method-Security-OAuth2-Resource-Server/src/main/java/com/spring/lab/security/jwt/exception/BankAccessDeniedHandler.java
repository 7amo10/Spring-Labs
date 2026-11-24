package com.spring.lab.security.jwt.exception;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.spring.lab.security.jwt.dto.SecurityErrorResponse;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.LocalDateTime;

@Component
public class BankAccessDeniedHandler implements AccessDeniedHandler {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException accessDeniedException)
            throws IOException, ServletException {
        response.setStatus(HttpStatus.FORBIDDEN.value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);

        SecurityErrorResponse errorResponse = new SecurityErrorResponse(
                java.time.Instant.now().toString(),
                HttpStatus.FORBIDDEN.value(),
                "Access Denied",
                "Insufficient privileges or SpEL authorization guard evaluation rejected request: " + accessDeniedException.getMessage(),
                request.getRequestURI()
        );

        response.getWriter().write(objectMapper.writeValueAsString(errorResponse));
    }
}
