package com.spring.lab.security.repository;

import com.spring.lab.security.entity.BankLoan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BankLoanRepository extends JpaRepository<BankLoan, Long> {
    List<BankLoan> findByBankUserEmailOrderByStartDateDesc(String email);
}
