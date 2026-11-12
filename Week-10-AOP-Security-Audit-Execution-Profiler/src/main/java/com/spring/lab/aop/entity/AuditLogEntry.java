package com.spring.lab.aop.entity;

import java.time.Instant;

public class AuditLogEntry {

    private final Instant timestamp;
    private final String aspectName;
    private final String adviceType;
    private final String targetMethod;
    private final String payloadDetails;

    public AuditLogEntry(String aspectName, String adviceType, String targetMethod, String payloadDetails) {
        this.timestamp = Instant.now();
        this.aspectName = aspectName;
        this.adviceType = adviceType;
        this.targetMethod = targetMethod;
        this.payloadDetails = payloadDetails;
    }

    public Instant getTimestamp() {
        return timestamp;
    }

    public String getAspectName() {
        return aspectName;
    }

    public String getAdviceType() {
        return adviceType;
    }

    public String getTargetMethod() {
        return targetMethod;
    }

    public String getPayloadDetails() {
        return payloadDetails;
    }

    @Override
    public String toString() {
        return "[" + timestamp + "] " + aspectName + " (" + adviceType + ") -> " + targetMethod + " | " + payloadDetails;
    }
}
