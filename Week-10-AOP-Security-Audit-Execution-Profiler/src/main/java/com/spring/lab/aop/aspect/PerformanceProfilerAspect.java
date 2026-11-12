package com.spring.lab.aop.aspect;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Aspect
@Component
@Order(2)
public class PerformanceProfilerAspect {

    private static final Logger log = LoggerFactory.getLogger(PerformanceProfilerAspect.class);
    private static final long SLA_THRESHOLD_MS = 100;

    private final AuditRegistry auditRegistry;

    @Autowired
    public PerformanceProfilerAspect(AuditRegistry auditRegistry) {
        this.auditRegistry = auditRegistry;
    }

    @Around("com.spring.lab.aop.aspect.ArchitecturePointcuts.forSettlementService()")
    public Object profileMethodExecution(ProceedingJoinPoint proceedingJoinPoint) throws Throwable {
        String methodName = proceedingJoinPoint.getSignature().toShortString();
        log.info("[ASPECT-ORDER-2][PROFILER] Starting timer for method: {}", methodName);

        long start = System.currentTimeMillis();
        Object result;

        try {
            result = proceedingJoinPoint.proceed();
        } catch (Throwable t) {
            long duration = System.currentTimeMillis() - start;
            log.error("[ASPECT-ORDER-2][PROFILER] Exception caught during execution of {} after {} ms: {}",
                    methodName, duration, t.getMessage());
            auditRegistry.recordEntry("PerformanceProfilerAspect", "@Around-Exception", methodName,
                    "duration=" + duration + "ms, error=" + t.getMessage());
            throw t;
        }

        long duration = System.currentTimeMillis() - start;
        log.info("[ASPECT-ORDER-2][PROFILER] Completed {} in {} ms", methodName, duration);

        if (duration >= SLA_THRESHOLD_MS) {
            String alert = "SLA BREACH ALERT: " + methodName + " took " + duration + " ms (threshold: " + SLA_THRESHOLD_MS + " ms)";
            log.warn("[ASPECT-ORDER-2][PROFILER] {}", alert);
            auditRegistry.recordSlaAlert(alert);
        }

        auditRegistry.recordEntry("PerformanceProfilerAspect", "@Around", methodName, "duration=" + duration + "ms");
        return result;
    }
}
