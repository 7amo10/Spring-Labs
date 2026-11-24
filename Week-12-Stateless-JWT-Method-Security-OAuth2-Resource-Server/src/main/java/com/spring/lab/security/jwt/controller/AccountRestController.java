package com.spring.lab.security.jwt.controller;

import com.spring.lab.security.jwt.entity.BankAccount;
import com.spring.lab.security.jwt.service.AccountService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/accounts")
public class AccountRestController {

    private final AccountService accountService;

    public AccountRestController(AccountService accountService) {
        this.accountService = accountService;
    }

    @GetMapping("/my-account")
    public ResponseEntity<BankAccount> getMyAccount(@RequestParam("email") String email) {
        BankAccount account = accountService.getAccountDetailsByEmail(email);
        if (account == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(account);
    }
}
