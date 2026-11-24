package com.spring.lab.security.jwt.repository;

import com.spring.lab.security.jwt.entity.BankLoan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BankLoanRepository extends JpaRepository<BankLoan, Long> {
    List<BankLoan> findByCustomerEmailOrderByStartDtDesc(String customerEmail);
}
