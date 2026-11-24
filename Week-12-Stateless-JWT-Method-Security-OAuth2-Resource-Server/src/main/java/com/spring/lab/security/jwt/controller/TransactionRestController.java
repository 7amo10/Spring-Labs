package com.spring.lab.security.jwt.controller;

import com.spring.lab.security.jwt.entity.BankTransaction;
import com.spring.lab.security.jwt.service.TransactionService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/transactions")
public class TransactionRestController {

    private final TransactionService transactionService;

    public TransactionRestController(TransactionService transactionService) {
        this.transactionService = transactionService;
    }

    @PostMapping("/batch-transfer")
    public ResponseEntity<List<BankTransaction>> executeBatchTransfer(@RequestBody List<BankTransaction> transactions) {
        List<BankTransaction> processed = transactionService.processBatchTransfers(transactions);
        return ResponseEntity.status(HttpStatus.CREATED).body(processed);
    }

    @GetMapping("/history")
    public ResponseEntity<List<BankTransaction>> getTransactionHistory(@RequestParam("email") String email) {
        List<BankTransaction> history = transactionService.getTransactionHistory(email);
        return ResponseEntity.ok(history);
    }
}
