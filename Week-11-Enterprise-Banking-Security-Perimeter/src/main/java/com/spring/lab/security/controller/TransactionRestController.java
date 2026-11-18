package com.spring.lab.security.controller;

import com.spring.lab.security.entity.BankTransaction;
import com.spring.lab.security.repository.BankTransactionRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/transactions")
public class TransactionRestController {

    private final BankTransactionRepository bankTransactionRepository;

    public TransactionRestController(BankTransactionRepository bankTransactionRepository) {
        this.bankTransactionRepository = bankTransactionRepository;
    }

    @GetMapping("/my-balance")
    public ResponseEntity<List<BankTransaction>> getTransactionHistory(@RequestParam Long accountNumber) {
        List<BankTransaction> transactions = bankTransactionRepository.findByAccountNumberOrderByTransactionDateDesc(accountNumber);
        return ResponseEntity.ok(transactions);
    }
}
