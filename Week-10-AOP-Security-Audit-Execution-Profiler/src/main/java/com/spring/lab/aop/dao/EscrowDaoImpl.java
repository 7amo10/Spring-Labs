package com.spring.lab.aop.dao;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

@Repository
public class EscrowDaoImpl implements EscrowDao {

    private static final Logger log = LoggerFactory.getLogger(EscrowDaoImpl.class);

    @Override
    public boolean verifyEscrowAccount(String accountNumber) {
        log.info("[ESCROW-DAO] Verifying escrow eligibility for account: {}", accountNumber);
        return accountNumber != null && !accountNumber.isBlank();
    }

    @Override
    public void holdFundsInEscrow(String transactionId, double amount) {
        log.info("[ESCROW-DAO] Holding funds in escrow: txnId={}, amount={}", transactionId, amount);
    }
}
