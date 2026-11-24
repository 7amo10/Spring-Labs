package com.spring.lab.security.jwt.filter;

import com.spring.lab.security.jwt.config.SecurityConstants;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.env.Environment;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.crypto.SecretKey;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.stream.Collectors;

public class JwtTokenGeneratorFilter extends OncePerRequestFilter {

    private static final Logger log = LoggerFactory.getLogger(JwtTokenGeneratorFilter.class);

    private final String secretKeyString;

    public JwtTokenGeneratorFilter(String secretKeyString) {
        this.secretKeyString = secretKeyString;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication != null && authentication.isAuthenticated() && !"anonymousUser".equals(authentication.getName())) {
            String secret = (secretKeyString != null && !secretKeyString.isBlank())
                    ? secretKeyString
                    : SecurityConstants.JWT_SECRET_DEFAULT_VALUE;

            SecretKey secretKey = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));

            String authorities = authentication.getAuthorities().stream()
                    .map(GrantedAuthority::getAuthority)
                    .collect(Collectors.joining(","));

            long now = System.currentTimeMillis();
            Date issuedAt = new Date(now);
            Date expiration = new Date(now + SecurityConstants.TOKEN_VALIDITY_MILLISECONDS);

            String jwt = Jwts.builder()
                    .issuer(SecurityConstants.ISSUER)
                    .subject(SecurityConstants.TOKEN_SUBJECT)
                    .claim(SecurityConstants.CLAIM_USERNAME, authentication.getName())
                    .claim(SecurityConstants.CLAIM_AUTHORITIES, authorities)
                    .issuedAt(issuedAt)
                    .expiration(expiration)
                    .signWith(secretKey)
                    .compact();

            log.info("Generated signed JWT token for principal '{}' with authorities '{}'", authentication.getName(), authorities);
            response.setHeader(SecurityConstants.JWT_HEADER, SecurityConstants.TOKEN_PREFIX + jwt);
        }

        filterChain.doFilter(request, response);
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
        String path = request.getRequestURI();
        if (path == null || path.isBlank()) {
            path = request.getServletPath();
        }
        boolean isAuthEndpoint = path != null && (
                path.endsWith("/api/v1/auth/login") ||
                path.endsWith("/api/v1/user/profile")
        );
        return !isAuthEndpoint;
    }
}
