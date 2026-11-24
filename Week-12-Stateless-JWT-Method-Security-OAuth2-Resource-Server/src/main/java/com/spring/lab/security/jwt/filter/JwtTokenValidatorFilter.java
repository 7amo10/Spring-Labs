package com.spring.lab.security.jwt.filter;

import com.spring.lab.security.jwt.config.SecurityConstants;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.crypto.SecretKey;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

public class JwtTokenValidatorFilter extends OncePerRequestFilter {

    private static final Logger log = LoggerFactory.getLogger(JwtTokenValidatorFilter.class);

    private final String secretKeyString;

    public JwtTokenValidatorFilter(String secretKeyString) {
        this.secretKeyString = secretKeyString;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        String authHeader = request.getHeader(SecurityConstants.JWT_HEADER);

        if (StringUtils.hasText(authHeader) && authHeader.trim().toLowerCase().startsWith("bearer ")) {
            String jwt = authHeader.trim().substring(SecurityConstants.TOKEN_PREFIX.length()).trim();

            try {
                String secret = (secretKeyString != null && !secretKeyString.isBlank())
                        ? secretKeyString
                        : SecurityConstants.JWT_SECRET_DEFAULT_VALUE;

                SecretKey secretKey = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));

                Claims claims = Jwts.parser()
                        .verifyWith(secretKey)
                        .build()
                        .parseSignedClaims(jwt)
                        .getPayload();

                String username = String.valueOf(claims.get(SecurityConstants.CLAIM_USERNAME));
                String authorities = String.valueOf(claims.get(SecurityConstants.CLAIM_AUTHORITIES));

                if (StringUtils.hasText(username) && !"null".equals(username)) {
                    Authentication authentication = new UsernamePasswordAuthenticationToken(
                            username,
                            null,
                            AuthorityUtils.commaSeparatedStringToAuthorityList(authorities)
                    );
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                    log.debug("Successfully validated JWT for user '{}' with authorities '{}'", username, authorities);
                }
            } catch (ExpiredJwtException ex) {
                log.warn("Expired JWT token received: {}", ex.getMessage());
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.setContentType("application/json");
                response.getWriter().write("{\"error\":\"Token has expired\",\"message\":\"" + ex.getMessage() + "\"}");
                return;
            } catch (JwtException | IllegalArgumentException ex) {
                log.warn("Invalid JWT token received: {}", ex.getMessage());
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.setContentType("application/json");
                response.getWriter().write("{\"error\":\"Invalid Token received\",\"message\":\"" + ex.getMessage() + "\"}");
                return;
            }
        }

        filterChain.doFilter(request, response);
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
        // Bypass token validation on login, registration, public notices, and federated endpoints
        String path = request.getServletPath();
        return path.equals("/api/v1/auth/login")
                || path.equals("/api/v1/auth/register")
                || path.equals("/api/v1/notices")
                || path.startsWith("/api/v1/federated/")
                || path.startsWith("/h2-console");
    }
}
