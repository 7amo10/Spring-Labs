package com.spring.lab.security.repository;

import com.spring.lab.security.entity.BankAccount;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BankAccountRepository extends JpaRepository<BankAccount, Long> {
    Optional<BankAccount> findByAccountNumber(Long accountNumber);
    List<BankAccount> findByBankUserEmail(String email);
    Optional<BankAccount> findByAccountNumberAndBankUserEmail(Long accountNumber, String email);
}
