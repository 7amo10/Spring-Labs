package com.spring.lab.security.exception;

public class CompromisedPasswordException extends RuntimeException {
    public CompromisedPasswordException(String message) {
        super(message);
    }
}
