package com.spring.lab.security.jwt.service;

import com.spring.lab.security.jwt.entity.BankAccount;
import com.spring.lab.security.jwt.repository.BankAccountRepository;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AccountService {

    private final BankAccountRepository accountRepository;

    public AccountService(BankAccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }

    @Transactional(readOnly = true)
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    @PostAuthorize("returnObject != null and (returnObject.customerEmail == authentication.name or hasRole('ADMIN'))")
    public BankAccount getAccountDetailsByEmail(String email) {
        return accountRepository.findByCustomerEmail(email).orElse(null);
    }

    @Transactional
    public BankAccount createOrUpdateAccount(BankAccount account) {
        return accountRepository.save(account);
    }
}
