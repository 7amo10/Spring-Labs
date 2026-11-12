package com.spring.lab.aop.rest;

import com.spring.lab.aop.aspect.AuditRegistry;
import com.spring.lab.aop.entity.AuditLogEntry;
import com.spring.lab.aop.entity.OrderTransaction;
import com.spring.lab.aop.service.PaymentSettlementService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/{version}/orders")
public class OrderAuditRestController {

    private final PaymentSettlementService paymentSettlementService;
    private final AuditRegistry auditRegistry;

    @Autowired
    public OrderAuditRestController(PaymentSettlementService paymentSettlementService, AuditRegistry auditRegistry) {
        this.paymentSettlementService = paymentSettlementService;
        this.auditRegistry = auditRegistry;
    }

    // Spring Boot 4 / Spring Framework 7 Built-In Path-Segment API Versioning: Version 1
    @GetMapping(version = "1")
    public Map<String, Object> getAuditOverviewV1() {
        Map<String, Object> response = new HashMap<>();
        response.put("apiVersion", "v1.0-legacy");
        response.put("settledOrdersCount", paymentSettlementService.querySettledTransactions().size());
        response.put("auditEntriesCount", auditRegistry.getAuditEntries().size());
        return response;
    }

    // Spring Boot 4 / Spring Framework 7 Built-In Path-Segment API Versioning: Version 2
    @GetMapping(version = "2")
    public Map<String, Object> getAuditOverviewV2() {
        Map<String, Object> response = new HashMap<>();
        response.put("apiVersion", "v2.0-modern");
        response.put("settledTransactions", paymentSettlementService.querySettledTransactions());
        response.put("auditLogTrail", auditRegistry.getAuditEntries());
        response.put("slaAlerts", auditRegistry.getSlaAlerts());
        return response;
    }

    // Version 2 POST endpoint for submitting new order settlements
    @PostMapping(version = "2")
    public ResponseEntity<OrderTransaction> submitOrderV2(@RequestBody OrderTransaction transaction) {
        OrderTransaction settled = paymentSettlementService.settleOrder(transaction, false);
        return new ResponseEntity<>(settled, HttpStatus.CREATED);
    }

    @GetMapping("/telemetry/sla-alerts")
    public List<String> getSlaAlerts() {
        return auditRegistry.getSlaAlerts();
    }
}
