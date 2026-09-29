package com.spring.lab.overview.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.Map;

@RestController
@RequestMapping
public class SystemInfoController {

    @Value("${coach.name}")
    private String coachName;

    @Value("${team.name}")
    private String teamName;

    @Value("${app.environment}")
    private String environment;

    @Value("${cluster.node.id}")
    private String clusterNodeId;

    @Value("${cluster.region}")
    private String clusterRegion;

    @GetMapping("/")
    public Map<String, String> rootEndpoint() {
        return Map.of(
                "service", "Enterprise Service Metadata & Health Gateway",
                "status", "ACTIVE",
                "documentation", "/api/v1/system-info",
                "actuator", "/actuator"
        );
    }

    @GetMapping("/api/v1/system-info")
    public Map<String, Object> getSystemMetadata() {
        Map<String, Object> response = new LinkedHashMap<>();
        response.put("service", "Enterprise Service Metadata & Health Gateway");
        response.put("coach", coachName);
        response.put("team", teamName);
        response.put("environment", environment);
        response.put("nodeId", clusterNodeId);
        response.put("region", clusterRegion);
        response.put("status", "HEALTHY");
        response.put("timestamp", Instant.now().toString());
        return response;
    }
}
