package com.spring.lab.security.jwt.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/federated")
public class FederatedResourceRestController {

    @GetMapping("/profile")
    public ResponseEntity<Map<String, Object>> getFederatedProfile(Authentication authentication) {
        Map<String, Object> profile = new HashMap<>();
        profile.put("principal", authentication.getName());
        profile.put("authorities", authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList()));
        profile.put("idp", "Keycloak-Federated-Gateway");
        profile.put("timestamp", LocalDateTime.now().toString());
        return ResponseEntity.ok(profile);
    }

    @GetMapping("/admin/audit")
    public ResponseEntity<Map<String, Object>> getFederatedAdminAudit(Authentication authentication) {
        Map<String, Object> audit = new HashMap<>();
        audit.put("status", "SUCCESS");
        audit.put("operator", authentication.getName());
        audit.put("scope", "REALM_ADMIN");
        audit.put("auditTimestamp", LocalDateTime.now().toString());
        return ResponseEntity.ok(audit);
    }
}
