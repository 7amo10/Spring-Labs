package com.spring.lab.security.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.UUID;

public class SecurityAuditFilter extends OncePerRequestFilter {

    private static final Logger log = LoggerFactory.getLogger(SecurityAuditFilter.class);
    public static final String TRACE_HEADER = "X-ApexBank-Audit-Trace";

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        String traceId = request.getHeader(TRACE_HEADER);
        if (traceId == null || traceId.isBlank()) {
            traceId = "TRC-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        }

        response.setHeader(TRACE_HEADER, traceId);
        log.info("[SECURITY-AUDIT-FILTER][TraceID: {}] Incoming {} request to URI: {}",
                traceId, request.getMethod(), request.getRequestURI());

        filterChain.doFilter(request, response);
    }
}
