package com.spring.lab.security.jwt.entity;

import jakarta.persistence.*;

import java.time.LocalDate;

@Entity
@Table(name = "bank_loans")
public class BankLoan {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "loan_number")
    private Long loanNumber;

    @Column(name = "customer_email", nullable = false, length = 120)
    private String customerEmail;

    @Column(name = "start_dt", nullable = false)
    private LocalDate startDt;

    @Column(name = "loan_type", nullable = false, length = 50)
    private String loanType;

    @Column(name = "total_loan", nullable = false)
    private Double totalLoan;

    @Column(name = "amount_paid", nullable = false)
    private Double amountPaid;

    @Column(name = "outstanding_amount", nullable = false)
    private Double outstandingAmount;

    @Column(name = "create_dt", nullable = false)
    private LocalDate createDt;

    public BankLoan() {
    }

    public BankLoan(String customerEmail, LocalDate startDt, String loanType, Double totalLoan, Double amountPaid, Double outstandingAmount, LocalDate createDt) {
        this.customerEmail = customerEmail;
        this.startDt = startDt;
        this.loanType = loanType;
        this.totalLoan = totalLoan;
        this.amountPaid = amountPaid;
        this.outstandingAmount = outstandingAmount;
        this.createDt = createDt;
    }

    public Long getLoanNumber() {
        return loanNumber;
    }

    public void setLoanNumber(Long loanNumber) {
        this.loanNumber = loanNumber;
    }

    public String getCustomerEmail() {
        return customerEmail;
    }

    public void setCustomerEmail(String customerEmail) {
        this.customerEmail = customerEmail;
    }

    public LocalDate getStartDt() {
        return startDt;
    }

    public void setStartDt(LocalDate startDt) {
        this.startDt = startDt;
    }

    public String getLoanType() {
        return loanType;
    }

    public void setLoanType(String loanType) {
        this.loanType = loanType;
    }

    public Double getTotalLoan() {
        return totalLoan;
    }

    public void setTotalLoan(Double totalLoan) {
        this.totalLoan = totalLoan;
    }

    public Double getAmountPaid() {
        return amountPaid;
    }

    public void setAmountPaid(Double amountPaid) {
        this.amountPaid = amountPaid;
    }

    public Double getOutstandingAmount() {
        return outstandingAmount;
    }

    public void setOutstandingAmount(Double outstandingAmount) {
        this.outstandingAmount = outstandingAmount;
    }

    public LocalDate getCreateDt() {
        return createDt;
    }

    public void setCreateDt(LocalDate createDt) {
        this.createDt = createDt;
    }
}
