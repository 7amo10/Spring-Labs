package com.spring.lab.security.event;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.event.EventListener;
import org.springframework.security.authentication.event.AbstractAuthenticationFailureEvent;
import org.springframework.security.authentication.event.AuthenticationSuccessEvent;
import org.springframework.stereotype.Component;

import java.util.concurrent.atomic.AtomicInteger;

@Component
public class SecurityAuthenticationEventListener {

    private static final Logger log = LoggerFactory.getLogger(SecurityAuthenticationEventListener.class);

    private final AtomicInteger successCounter = new AtomicInteger(0);
    private final AtomicInteger failureCounter = new AtomicInteger(0);

    @EventListener
    public void onAuthenticationSuccess(AuthenticationSuccessEvent successEvent) {
        successCounter.incrementAndGet();
        log.info("[SECURITY-EVENT][SUCCESS] User authenticated successfully: {}",
                successEvent.getAuthentication().getName());
    }

    @EventListener
    public void onAuthenticationFailure(AbstractAuthenticationFailureEvent failureEvent) {
        failureCounter.incrementAndGet();
        log.warn("[SECURITY-EVENT][FAILURE] Authentication failed for principal: {}, reason: {}",
                failureEvent.getAuthentication().getName(),
                failureEvent.getException().getMessage());
    }

    public int getSuccessCount() {
        return successCounter.get();
    }

    public int getFailureCount() {
        return failureCounter.get();
    }

    public void resetCounters() {
        successCounter.set(0);
        failureCounter.set(0);
    }
}
