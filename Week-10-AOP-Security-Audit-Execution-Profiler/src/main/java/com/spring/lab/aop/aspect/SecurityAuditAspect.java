package com.spring.lab.aop.aspect;

import com.spring.lab.aop.entity.OrderTransaction;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Aspect
@Component
@Order(1)
public class SecurityAuditAspect {

    private static final Logger log = LoggerFactory.getLogger(SecurityAuditAspect.class);

    private final AuditRegistry auditRegistry;

    @Autowired
    public SecurityAuditAspect(AuditRegistry auditRegistry) {
        this.auditRegistry = auditRegistry;
    }

    @Before("com.spring.lab.aop.aspect.ArchitecturePointcuts.forDaoNoGetterSetter()")
    public void auditDaoInvocation(JoinPoint joinPoint) {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        String methodName = signature.toShortString();
        log.info("[ASPECT-ORDER-1][AUDIT] Intercepting DAO operation: {}", methodName);

        StringBuilder payloadSummary = new StringBuilder();
        Object[] args = joinPoint.getArgs();

        if (args != null && args.length > 0) {
            for (Object arg : args) {
                if (arg instanceof OrderTransaction txn) {
                    payloadSummary.append("txnId=").append(txn.getTransactionId())
                            .append(", secClass=").append(txn.getSecurityClassification())
                            .append(", amount=").append(txn.getAmount()).append("; ");
                    log.info("[ASPECT-ORDER-1][AUDIT] Security inspection: Txn {} classified as {}",
                            txn.getTransactionId(), txn.getSecurityClassification());
                } else {
                    payloadSummary.append("arg=").append(arg).append("; ");
                }
            }
        } else {
            payloadSummary.append("no-args");
        }

        auditRegistry.recordEntry("SecurityAuditAspect", "@Before", methodName, payloadSummary.toString());
    }
}
