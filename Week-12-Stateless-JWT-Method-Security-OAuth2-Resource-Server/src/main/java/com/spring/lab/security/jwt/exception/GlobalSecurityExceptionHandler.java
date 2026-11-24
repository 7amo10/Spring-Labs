package com.spring.lab.security.jwt.exception;

import com.spring.lab.security.jwt.dto.SecurityErrorResponse;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;

@RestControllerAdvice
public class GlobalSecurityExceptionHandler {

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<SecurityErrorResponse> handleAccessDeniedException(AccessDeniedException ex, HttpServletRequest request) {
        SecurityErrorResponse error = new SecurityErrorResponse(
                java.time.Instant.now().toString(),
                HttpStatus.FORBIDDEN.value(),
                "Forbidden - Method Security Boundary",
                ex.getMessage(),
                request.getRequestURI()
        );
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(error);
    }

    @ExceptionHandler({BadCredentialsException.class, AuthenticationException.class})
    public ResponseEntity<SecurityErrorResponse> handleAuthenticationException(Exception ex, HttpServletRequest request) {
        SecurityErrorResponse error = new SecurityErrorResponse(
                java.time.Instant.now().toString(),
                HttpStatus.UNAUTHORIZED.value(),
                "Unauthorized - Invalid Credentials or Token",
                ex.getMessage(),
                request.getRequestURI()
        );
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(error);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<SecurityErrorResponse> handleIllegalArgumentException(IllegalArgumentException ex, HttpServletRequest request) {
        SecurityErrorResponse error = new SecurityErrorResponse(
                java.time.Instant.now().toString(),
                HttpStatus.BAD_REQUEST.value(),
                "Bad Request - Validation Failure",
                ex.getMessage(),
                request.getRequestURI()
        );
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }
}
