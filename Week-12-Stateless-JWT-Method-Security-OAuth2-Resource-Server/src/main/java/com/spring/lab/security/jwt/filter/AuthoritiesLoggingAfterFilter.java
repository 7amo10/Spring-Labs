package com.spring.lab.security.jwt.filter;

import jakarta.servlet.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.io.IOException;

public class AuthoritiesLoggingAfterFilter implements Filter {

    private static final Logger log = LoggerFactory.getLogger(AuthoritiesLoggingAfterFilter.class);

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated() && !"anonymousUser".equals(authentication.getName())) {
            log.info("Principal '{}' authenticated successfully with granted authorities {}",
                    authentication.getName(), authentication.getAuthorities());
        }
        chain.doFilter(request, response);
    }
}
