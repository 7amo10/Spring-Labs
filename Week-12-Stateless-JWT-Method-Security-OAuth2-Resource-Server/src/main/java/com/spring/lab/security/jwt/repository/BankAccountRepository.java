package com.spring.lab.security.jwt.repository;

import com.spring.lab.security.jwt.entity.BankAccount;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface BankAccountRepository extends JpaRepository<BankAccount, Long> {
    Optional<BankAccount> findByCustomerEmail(String customerEmail);
}
