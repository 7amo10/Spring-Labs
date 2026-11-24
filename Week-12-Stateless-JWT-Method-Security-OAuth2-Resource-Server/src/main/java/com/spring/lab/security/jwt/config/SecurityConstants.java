package com.spring.lab.security.jwt.config;

public final class SecurityConstants {

    private SecurityConstants() {
        // Utility class
    }

    public static final String JWT_SECRET_DEFAULT_VALUE = "ApexBankEnterpriseSecretKeyForStatelessJwtSecurityTesting2026SecureKey!";
    public static final String JWT_SECRET_PROPERTY = "application.security.jwt.secret";
    public static final String JWT_HEADER = "Authorization";
    public static final String TOKEN_PREFIX = "Bearer ";
    public static final String ISSUER = "ApexBank-Security-Gateway";
    public static final String TOKEN_SUBJECT = "ApexBank User Access Token";
    public static final long TOKEN_VALIDITY_MILLISECONDS = 2 * 60 * 60 * 1000L; // 2 hours

    public static final String CLAIM_USERNAME = "username";
    public static final String CLAIM_AUTHORITIES = "authorities";
    public static final String CLAIM_ROLES = "roles";
}
