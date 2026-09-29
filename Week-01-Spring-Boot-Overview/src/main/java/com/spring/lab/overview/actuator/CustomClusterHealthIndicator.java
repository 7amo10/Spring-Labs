package com.spring.lab.overview.actuator;

import org.springframework.boot.health.contributor.Health;
import org.springframework.boot.health.contributor.HealthIndicator;
import org.springframework.stereotype.Component;

import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;

@Component
public class CustomClusterHealthIndicator implements HealthIndicator {

    private final MemoryMXBean memoryBean = ManagementFactory.getMemoryMXBean();

    @Override
    public Health health() {
        long usedHeapMb = memoryBean.getHeapMemoryUsage().getUsed() / (1024 * 1024);
        long maxHeapMb = memoryBean.getHeapMemoryUsage().getMax() / (1024 * 1024);

        if (maxHeapMb > 0 && usedHeapMb > (maxHeapMb * 0.95)) {
            return Health.down()
                    .withDetail("clusterNode", "node-omega-01")
                    .withDetail("memoryState", "CRITICAL")
                    .withDetail("heapUsedMb", usedHeapMb)
                    .withDetail("heapMaxMb", maxHeapMb)
                    .build();
        }

        return Health.up()
                .withDetail("clusterNode", "node-omega-01")
                .withDetail("gatewayStatus", "OPERATIONAL")
                .withDetail("heapUsedMb", usedHeapMb)
                .withDetail("heapMaxMb", maxHeapMb)
                .build();
    }
}
