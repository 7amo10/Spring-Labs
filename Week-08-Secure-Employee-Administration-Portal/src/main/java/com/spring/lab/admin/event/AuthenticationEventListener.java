package com.spring.lab.admin.event;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.event.EventListener;
import org.springframework.security.authentication.event.AbstractAuthenticationFailureEvent;
import org.springframework.security.authentication.event.AuthenticationSuccessEvent;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

/**
 * Event listener auditing authentication lifecycle events (success and failure)
 * to support security telemetry and intrusion detection.
 */
@Component
public class AuthenticationEventListener {

    private static final Logger log = LoggerFactory.getLogger(AuthenticationEventListener.class);

    @EventListener
    public void onAuthenticationSuccess(AuthenticationSuccessEvent event) {
        Authentication auth = event.getAuthentication();
        log.info("SECURITY_AUDIT: Authentication SUCCESS for user '{}' with authorities: {}",
                auth.getName(), auth.getAuthorities());
    }

    @EventListener
    public void onAuthenticationFailure(AbstractAuthenticationFailureEvent event) {
        Authentication auth = event.getAuthentication();
        String username = (auth != null) ? auth.getName() : "UNKNOWN";
        log.warn("SECURITY_AUDIT: Authentication FAILURE for user '{}' - Exception: {}",
                username, event.getException().getMessage());
    }
}
