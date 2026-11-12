package com.spring.lab.aop.dao;

public interface EscrowDao {

    boolean verifyEscrowAccount(String accountNumber);

    void holdFundsInEscrow(String transactionId, double amount);
}
