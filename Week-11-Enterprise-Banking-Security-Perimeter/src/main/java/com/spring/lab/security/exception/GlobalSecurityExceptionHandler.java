package com.spring.lab.security.exception;

import com.spring.lab.security.dto.SecurityErrorResponse;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalSecurityExceptionHandler {

    @ExceptionHandler(CompromisedPasswordException.class)
    public ResponseEntity<SecurityErrorResponse> handleCompromisedPassword(CompromisedPasswordException ex,
                                                                           HttpServletRequest request) {
        SecurityErrorResponse error = new SecurityErrorResponse(
                HttpStatus.BAD_REQUEST.value(),
                HttpStatus.BAD_REQUEST.getReasonPhrase(),
                ex.getMessage(),
                request.getRequestURI()
        );
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<SecurityErrorResponse> handleIllegalArgument(IllegalArgumentException ex,
                                                                        HttpServletRequest request) {
        SecurityErrorResponse error = new SecurityErrorResponse(
                HttpStatus.BAD_REQUEST.value(),
                HttpStatus.BAD_REQUEST.getReasonPhrase(),
                ex.getMessage(),
                request.getRequestURI()
        );
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }
}
