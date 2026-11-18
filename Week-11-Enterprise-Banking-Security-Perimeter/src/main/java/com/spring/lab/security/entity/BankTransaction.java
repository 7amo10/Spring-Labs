package com.spring.lab.security.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(name = "bank_transactions")
public class BankTransaction {

    @Id
    @Column(name = "transaction_id", length = 64)
    private String transactionId;

    @Column(name = "account_number", nullable = false)
    private Long accountNumber;

    @Column(name = "transaction_date", nullable = false)
    private LocalDate transactionDate;

    @Column(name = "transaction_summary", nullable = false, length = 200)
    private String transactionSummary;

    @Column(name = "transaction_type", nullable = false, length = 30)
    private String transactionType;

    @Column(name = "transaction_amount", nullable = false, precision = 15, scale = 2)
    private BigDecimal transactionAmount;

    @Column(name = "closing_balance", nullable = false, precision = 15, scale = 2)
    private BigDecimal closingBalance;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private BankUser bankUser;

    public BankTransaction() {
        this.transactionId = UUID.randomUUID().toString();
        this.transactionDate = LocalDate.now();
    }

    public BankTransaction(Long accountNumber, LocalDate transactionDate, String transactionSummary,
                           String transactionType, BigDecimal transactionAmount, BigDecimal closingBalance, BankUser bankUser) {
        this.transactionId = UUID.randomUUID().toString();
        this.accountNumber = accountNumber;
        this.transactionDate = transactionDate;
        this.transactionSummary = transactionSummary;
        this.transactionType = transactionType;
        this.transactionAmount = transactionAmount;
        this.closingBalance = closingBalance;
        this.bankUser = bankUser;
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

    public LocalDate getTransactionDate() {
        return transactionDate;
    }

    public void setTransactionDate(LocalDate transactionDate) {
        this.transactionDate = transactionDate;
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

    public BigDecimal getTransactionAmount() {
        return transactionAmount;
    }

    public void setTransactionAmount(BigDecimal transactionAmount) {
        this.transactionAmount = transactionAmount;
    }

    public BigDecimal getClosingBalance() {
        return closingBalance;
    }

    public void setClosingBalance(BigDecimal closingBalance) {
        this.closingBalance = closingBalance;
    }

    public BankUser getBankUser() {
        return bankUser;
    }

    public void setBankUser(BankUser bankUser) {
        this.bankUser = bankUser;
    }
}
