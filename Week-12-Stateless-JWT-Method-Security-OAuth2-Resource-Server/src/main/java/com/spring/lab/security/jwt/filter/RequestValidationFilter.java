package com.spring.lab.security.jwt.filter;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

public class RequestValidationFilter implements Filter {

    private static final Logger log = LoggerFactory.getLogger(RequestValidationFilter.class);

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse res = (HttpServletResponse) response;

        // 1. Inspect Basic Auth Header if present
        String header = req.getHeader(HttpHeaders.AUTHORIZATION);
        if (StringUtils.hasText(header) && StringUtils.startsWithIgnoreCase(header, "Basic ")) {
            try {
                byte[] base64Token = header.substring(6).trim().getBytes(StandardCharsets.UTF_8);
                byte[] decoded = Base64.getDecoder().decode(base64Token);
                String credentials = new String(decoded, StandardCharsets.UTF_8);
                int colonIndex = credentials.indexOf(":");
                if (colonIndex != -1) {
                    String username = credentials.substring(0, colonIndex);
                    if (username.toLowerCase().contains("prohibited-client") || username.toLowerCase().contains("malicious-test")) {
                        log.warn("Blocked request containing prohibited username in Basic Auth: {}", username);
                        res.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                        res.getWriter().write("{\"error\":\"Prohibited username pattern detected\"}");
                        return;
                    }
                }
            } catch (IllegalArgumentException e) {
                log.warn("Failed to decode Basic authentication header: {}", e.getMessage());
                res.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                res.getWriter().write("{\"error\":\"Malformed Basic authentication header\"}");
                return;
            }
        }

        // 2. Inspect query parameters for prohibited test injections
        String queryParam = req.getParameter("blocked_tag");
        if (StringUtils.hasText(queryParam) && "true".equalsIgnoreCase(queryParam)) {
            log.warn("Blocked request with blocked_tag query parameter");
            res.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            res.getWriter().write("{\"error\":\"Request rejected by validation filter\"}");
            return;
        }

        chain.doFilter(request, response);
    }
}
