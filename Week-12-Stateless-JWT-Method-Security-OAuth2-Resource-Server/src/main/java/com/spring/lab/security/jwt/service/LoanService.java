package com.spring.lab.security.jwt.service;

import com.spring.lab.security.jwt.dto.LoanApplicationRequest;
import com.spring.lab.security.jwt.entity.BankLoan;
import com.spring.lab.security.jwt.repository.BankLoanRepository;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
public class LoanService {

    private final BankLoanRepository loanRepository;

    public LoanService(BankLoanRepository loanRepository) {
        this.loanRepository = loanRepository;
    }

    @Transactional
    @PreAuthorize("hasRole('USER') and #request.loanAmount <= 1000000")
    public BankLoan applyForLoan(LoanApplicationRequest request) {
        BankLoan loan = new BankLoan(
                request.customerEmail(),
                LocalDate.now(),
                request.loanType(),
                request.loanAmount(),
                0.0,
                request.loanAmount(),
                LocalDate.now()
        );
        return loanRepository.save(loan);
    }

    @Transactional(readOnly = true)
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public List<BankLoan> getLoansForCustomer(String email) {
        return loanRepository.findByCustomerEmailOrderByStartDtDesc(email);
    }
}
