package com.spring.lab.aop.aspect;

import com.spring.lab.aop.entity.OrderTransaction;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.util.List;

@Aspect
@Component
@Order(3)
public class DataSanitizationAspect {

    private static final Logger log = LoggerFactory.getLogger(DataSanitizationAspect.class);

    private final AuditRegistry auditRegistry;

    @Autowired
    public DataSanitizationAspect(AuditRegistry auditRegistry) {
        this.auditRegistry = auditRegistry;
    }

    @AfterReturning(
            pointcut = "com.spring.lab.aop.aspect.ArchitecturePointcuts.forDaoNoGetterSetter()",
            returning = "result"
    )
    public void sanitizeReturnedData(JoinPoint joinPoint, Object result) {
        String methodName = joinPoint.getSignature().toShortString();
        log.info("[ASPECT-ORDER-3][SANITIZATION] Post-processing output from: {}", methodName);

        if (result instanceof List<?> rawList) {
            int sanitizedCount = 0;
            for (Object item : rawList) {
                if (item instanceof OrderTransaction txn) {
                    // Mask sensitive account number: ACC-1111-2222 -> ACC-****-2222
                    String acc = txn.getAccountNumber();
                    if (acc != null && acc.length() >= 8) {
                        String masked = acc.substring(0, 4) + "****" + acc.substring(acc.length() - 4);
                        txn.setAccountNumber(masked);
                    }
                    // Normalize currency to standard uppercase: usd -> USD
                    if (txn.getCurrency() != null) {
                        txn.setCurrency(txn.getCurrency().toUpperCase());
                    }
                    sanitizedCount++;
                }
            }
            log.info("[ASPECT-ORDER-3][SANITIZATION] Sanitized {} transactions in result list", sanitizedCount);
            auditRegistry.recordEntry("DataSanitizationAspect", "@AfterReturning", methodName,
                    "sanitizedCount=" + sanitizedCount);
        } else {
            auditRegistry.recordEntry("DataSanitizationAspect", "@AfterReturning", methodName,
                    "resultType=" + (result != null ? result.getClass().getSimpleName() : "null"));
        }
    }
}
