package com.spring.lab.aop.aspect;

import com.spring.lab.aop.entity.AuditLogEntry;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

@Component
public class AuditRegistry {

    private final List<AuditLogEntry> auditEntries = new CopyOnWriteArrayList<>();
    private final List<String> executionOrder = new CopyOnWriteArrayList<>();
    private final List<String> slaAlerts = new CopyOnWriteArrayList<>();

    public void recordEntry(String aspectName, String adviceType, String targetMethod, String payloadDetails) {
        auditEntries.add(new AuditLogEntry(aspectName, adviceType, targetMethod, payloadDetails));
        executionOrder.add(aspectName + ":" + adviceType);
    }

    public void recordSlaAlert(String alertMessage) {
        slaAlerts.add(alertMessage);
    }

    public List<AuditLogEntry> getAuditEntries() {
        return Collections.unmodifiableList(auditEntries);
    }

    public List<String> getExecutionOrder() {
        return Collections.unmodifiableList(executionOrder);
    }

    public List<String> getSlaAlerts() {
        return Collections.unmodifiableList(slaAlerts);
    }

    public void clear() {
        auditEntries.clear();
        executionOrder.clear();
        slaAlerts.clear();
    }
}
