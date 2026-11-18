package com.spring.lab.security.controller;

import com.spring.lab.security.entity.BankAccount;
import com.spring.lab.security.repository.BankAccountRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/accounts")
public class AccountRestController {

    private final BankAccountRepository bankAccountRepository;

    public AccountRestController(BankAccountRepository bankAccountRepository) {
        this.bankAccountRepository = bankAccountRepository;
    }

    @GetMapping("/my-account")
    public ResponseEntity<BankAccount> getAccountDetails(@RequestParam Long accountNumber) {
        return bankAccountRepository.findByAccountNumber(accountNumber)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/user")
    public ResponseEntity<List<BankAccount>> getUserAccounts(@RequestParam String email) {
        List<BankAccount> accounts = bankAccountRepository.findByBankUserEmail(email);
        return ResponseEntity.ok(accounts);
    }
}
