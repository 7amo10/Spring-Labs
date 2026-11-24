package com.spring.lab.security.jwt.filter;

import jakarta.servlet.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public class AuthoritiesLoggingAtFilter implements Filter {

    private static final Logger log = LoggerFactory.getLogger(AuthoritiesLoggingAtFilter.class);

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        log.debug("Authentication verification pipeline stage in progress...");
        chain.doFilter(request, response);
    }
}
