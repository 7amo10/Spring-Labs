package com.spring.lab.security.jwt.entity;

import jakarta.persistence.*;

import java.time.LocalDate;

@Entity
@Table(name = "bank_accounts")
public class BankAccount {

    @Id
    @Column(name = "account_number", nullable = false)
    private Long accountNumber;

    @Column(name = "account_type", nullable = false, length = 50)
    private String accountType;

    @Column(name = "branch_address", nullable = false, length = 200)
    private String branchAddress;

    @Column(name = "balance", nullable = false)
    private Double balance;

    @Column(name = "customer_email", nullable = false, length = 120)
    private String customerEmail;

    @Column(name = "create_dt", nullable = false)
    private LocalDate createDt;

    public BankAccount() {
    }

    public BankAccount(Long accountNumber, String accountType, String branchAddress, Double balance, String customerEmail, LocalDate createDt) {
        this.accountNumber = accountNumber;
        this.accountType = accountType;
        this.branchAddress = branchAddress;
        this.balance = balance;
        this.customerEmail = customerEmail;
        this.createDt = createDt;
    }

    public Long getAccountNumber() {
        return accountNumber;
    }

    public void setAccountNumber(Long accountNumber) {
        this.accountNumber = accountNumber;
    }

    public String getAccountType() {
        return accountType;
    }

    public void setAccountType(String accountType) {
        this.accountType = accountType;
    }

    public String getBranchAddress() {
        return branchAddress;
    }

    public void setBranchAddress(String branchAddress) {
        this.branchAddress = branchAddress;
    }

    public Double getBalance() {
        return balance;
    }

    public void setBalance(Double balance) {
        this.balance = balance;
    }

    public String getCustomerEmail() {
        return customerEmail;
    }

    public void setCustomerEmail(String customerEmail) {
        this.customerEmail = customerEmail;
    }

    public LocalDate getCreateDt() {
        return createDt;
    }

    public void setCreateDt(LocalDate createDt) {
        this.createDt = createDt;
    }
}
