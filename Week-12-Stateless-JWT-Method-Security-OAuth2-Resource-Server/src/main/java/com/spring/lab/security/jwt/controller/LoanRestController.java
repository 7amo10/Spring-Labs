package com.spring.lab.security.jwt.controller;

import com.spring.lab.security.jwt.dto.LoanApplicationRequest;
import com.spring.lab.security.jwt.entity.BankLoan;
import com.spring.lab.security.jwt.service.LoanService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/loans")
public class LoanRestController {

    private final LoanService loanService;

    public LoanRestController(LoanService loanService) {
        this.loanService = loanService;
    }

    @PostMapping("/apply")
    public ResponseEntity<BankLoan> applyForLoan(@RequestBody LoanApplicationRequest request) {
        BankLoan createdLoan = loanService.applyForLoan(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdLoan);
    }

    @GetMapping("/my-loans")
    public ResponseEntity<List<BankLoan>> getMyLoans(@RequestParam("email") String email) {
        List<BankLoan> loans = loanService.getLoansForCustomer(email);
        return ResponseEntity.ok(loans);
    }
}
