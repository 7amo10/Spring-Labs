package com.spring.lab.security.controller;

import com.spring.lab.security.entity.BankLoan;
import com.spring.lab.security.repository.BankLoanRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/loans")
public class LoanRestController {

    private final BankLoanRepository bankLoanRepository;

    public LoanRestController(BankLoanRepository bankLoanRepository) {
        this.bankLoanRepository = bankLoanRepository;
    }

    @GetMapping("/my-loans")
    public ResponseEntity<List<BankLoan>> getLoanDetails(@RequestParam String email) {
        List<BankLoan> loans = bankLoanRepository.findByBankUserEmailOrderByStartDateDesc(email);
        return ResponseEntity.ok(loans);
    }
}
