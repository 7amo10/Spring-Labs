package com.spring.lab.aop.aspect;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Aspect
@Component
@Order(4)
public class ExceptionLoggerAspect {

    private static final Logger log = LoggerFactory.getLogger(ExceptionLoggerAspect.class);

    private final AuditRegistry auditRegistry;

    @Autowired
    public ExceptionLoggerAspect(AuditRegistry auditRegistry) {
        this.auditRegistry = auditRegistry;
    }

    @AfterThrowing(
            pointcut = "com.spring.lab.aop.aspect.ArchitecturePointcuts.forAppFlow()",
            throwing = "theExc"
    )
    public void logException(JoinPoint joinPoint, Throwable theExc) {
        String methodName = joinPoint.getSignature().toShortString();
        log.error("[ASPECT-ORDER-4][EXCEPTION-LOGGER] Intercepted exception in method {}: {} - {}",
                methodName, theExc.getClass().getSimpleName(), theExc.getMessage());

        auditRegistry.recordEntry("ExceptionLoggerAspect", "@AfterThrowing", methodName,
                "exception=" + theExc.getClass().getSimpleName() + ": " + theExc.getMessage());
    }

    @After("com.spring.lab.aop.aspect.ArchitecturePointcuts.forAppFlow()")
    public void executeFinallyCleanup(JoinPoint joinPoint) {
        String methodName = joinPoint.getSignature().toShortString();
        log.info("[ASPECT-ORDER-4][FINALLY-CLEANUP] Executing @After finally advice on: {}", methodName);

        auditRegistry.recordEntry("ExceptionLoggerAspect", "@After", methodName, "contextCleanupCompleted");
    }
}
