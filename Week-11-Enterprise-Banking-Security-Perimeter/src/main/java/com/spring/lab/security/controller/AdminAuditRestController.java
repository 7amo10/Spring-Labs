package com.spring.lab.security.controller;

import com.spring.lab.security.event.SecurityAuthenticationEventListener;
import com.spring.lab.security.repository.BankUserRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/admin")
public class AdminAuditRestController {

    private final BankUserRepository bankUserRepository;
    private final SecurityAuthenticationEventListener securityEventListener;

    public AdminAuditRestController(BankUserRepository bankUserRepository,
                                   SecurityAuthenticationEventListener securityEventListener) {
        this.bankUserRepository = bankUserRepository;
        this.securityEventListener = securityEventListener;
    }

    @GetMapping("/audit")
    public ResponseEntity<Map<String, Object>> getSecurityAuditOverview() {
        long totalUsers = bankUserRepository.count();
        int successfulLogins = securityEventListener.getSuccessCount();
        int failedLogins = securityEventListener.getFailureCount();

        return ResponseEntity.ok(Map.of(
                "totalRegisteredBankUsers", totalUsers,
                "successfulLoginEvents", successfulLogins,
                "failedLoginEvents", failedLogins,
                "securityStatus", "SECURE_PERIMETER_ENFORCED"
        ));
    }
}
