package com.spring.lab.aop.entity;

import java.util.Objects;

public class OrderTransaction {

    private String transactionId;
    private String accountNumber;
    private double amount;
    private String currency;
    private String securityClassification;
    private String status;

    public OrderTransaction() {}

    public OrderTransaction(String transactionId, String accountNumber, double amount, String currency, String securityClassification) {
        this.transactionId = transactionId;
        this.accountNumber = accountNumber;
        this.amount = amount;
        this.currency = currency;
        this.securityClassification = securityClassification;
        this.status = "PENDING";
    }

    public OrderTransaction(String transactionId, String accountNumber, double amount, String currency, String securityClassification, String status) {
        this.transactionId = transactionId;
        this.accountNumber = accountNumber;
        this.amount = amount;
        this.currency = currency;
        this.securityClassification = securityClassification;
        this.status = status;
    }

    public String getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public void setAccountNumber(String accountNumber) {
        this.accountNumber = accountNumber;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public String getSecurityClassification() {
        return securityClassification;
    }

    public void setSecurityClassification(String securityClassification) {
        this.securityClassification = securityClassification;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof OrderTransaction that)) return false;
        return Double.compare(that.amount, amount) == 0 &&
                Objects.equals(transactionId, that.transactionId) &&
                Objects.equals(accountNumber, that.accountNumber) &&
                Objects.equals(currency, that.currency) &&
                Objects.equals(securityClassification, that.securityClassification) &&
                Objects.equals(status, that.status);
    }

    @Override
    public int hashCode() {
        return Objects.hash(transactionId, accountNumber, amount, currency, securityClassification, status);
    }

    @Override
    public String toString() {
        return "OrderTransaction{" +
                "transactionId='" + transactionId + '\'' +
                ", accountNumber='" + accountNumber + '\'' +
                ", amount=" + amount +
                ", currency='" + currency + '\'' +
                ", securityClassification='" + securityClassification + '\'' +
                ", status='" + status + '\'' +
                '}';
    }
}
