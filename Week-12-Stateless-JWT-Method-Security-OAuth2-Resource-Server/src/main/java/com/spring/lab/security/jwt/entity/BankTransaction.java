package com.spring.lab.security.jwt.entity;

import jakarta.persistence.*;

import java.time.LocalDate;

@Entity
@Table(name = "bank_transactions")
public class BankTransaction {

    @Id
    @Column(name = "transaction_id", nullable = false, length = 100)
    private String transactionId;

    @Column(name = "account_number", nullable = false)
    private Long accountNumber;

    @Column(name = "sender_email", nullable = false, length = 120)
    private String senderEmail;

    @Column(name = "transaction_dt", nullable = false)
    private LocalDate transactionDt;

    @Column(name = "transaction_summary", nullable = false, length = 200)
    private String transactionSummary;

    @Column(name = "transaction_type", nullable = false, length = 50)
    private String transactionType;

    @Column(name = "transaction_amt", nullable = false)
    private Double transactionAmt;

    @Column(name = "closing_balance", nullable = false)
    private Double closingBalance;

    public BankTransaction() {
    }

    public BankTransaction(String transactionId, Long accountNumber, String senderEmail, LocalDate transactionDt,
                           String transactionSummary, String transactionType, Double transactionAmt, Double closingBalance) {
        this.transactionId = transactionId;
        this.accountNumber = accountNumber;
        this.senderEmail = senderEmail;
        this.transactionDt = transactionDt;
        this.transactionSummary = transactionSummary;
        this.transactionType = transactionType;
        this.transactionAmt = transactionAmt;
        this.closingBalance = closingBalance;
    }

    public String getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
    }

    public Long getAccountNumber() {
        return accountNumber;
    }

    public void setAccountNumber(Long accountNumber) {
        this.accountNumber = accountNumber;
    }

    public String getSenderEmail() {
        return senderEmail;
    }

    public void setSenderEmail(String senderEmail) {
        this.senderEmail = senderEmail;
    }

    public LocalDate getTransactionDt() {
        return transactionDt;
    }

    public void setTransactionDt(LocalDate transactionDt) {
        this.transactionDt = transactionDt;
    }

    public String getTransactionSummary() {
        return transactionSummary;
    }

    public void setTransactionSummary(String transactionSummary) {
        this.transactionSummary = transactionSummary;
    }

    public String getTransactionType() {
        return transactionType;
    }

    public void setTransactionType(String transactionType) {
        this.transactionType = transactionType;
    }

    public Double getTransactionAmt() {
        return transactionAmt;
    }

    public void setTransactionAmt(Double transactionAmt) {
        this.transactionAmt = transactionAmt;
    }

    public Double getClosingBalance() {
        return closingBalance;
    }

    public void setClosingBalance(Double closingBalance) {
        this.closingBalance = closingBalance;
    }
}
