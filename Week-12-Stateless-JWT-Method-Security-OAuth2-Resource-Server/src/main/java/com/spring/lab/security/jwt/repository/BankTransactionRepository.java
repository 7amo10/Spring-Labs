package com.spring.lab.security.jwt.repository;

import com.spring.lab.security.jwt.entity.BankTransaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BankTransactionRepository extends JpaRepository<BankTransaction, String> {
    List<BankTransaction> findBySenderEmailOrderByTransactionDtDesc(String senderEmail);
    List<BankTransaction> findByAccountNumberOrderByTransactionDtDesc(Long accountNumber);
}
