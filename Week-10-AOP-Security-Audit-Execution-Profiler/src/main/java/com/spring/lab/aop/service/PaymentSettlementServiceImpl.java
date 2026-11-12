package com.spring.lab.aop.service;

import com.spring.lab.aop.dao.EscrowDao;
import com.spring.lab.aop.dao.OrderDao;
import com.spring.lab.aop.entity.OrderTransaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.TimeUnit;

@Service
public class PaymentSettlementServiceImpl implements PaymentSettlementService {

    private static final Logger log = LoggerFactory.getLogger(PaymentSettlementServiceImpl.class);

    private final OrderDao orderDao;
    private final EscrowDao escrowDao;

    @Autowired
    public PaymentSettlementServiceImpl(OrderDao orderDao, EscrowDao escrowDao) {
        this.orderDao = orderDao;
        this.escrowDao = escrowDao;
    }

    @Override
    public OrderTransaction settleOrder(OrderTransaction transaction, boolean simulateDelay) {
        log.info("[SERVICE] Settle order initiated for transaction: {}", transaction.getTransactionId());

        if (simulateDelay) {
            try {
                // Introduce simulated high-latency to breach SLA threshold (e.g. > 100ms)
                TimeUnit.MILLISECONDS.sleep(120);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                throw new RuntimeException("Settlement thread interrupted", e);
            }
        }

        boolean escrowOk = escrowDao.verifyEscrowAccount(transaction.getAccountNumber());
        if (escrowOk && transaction.getAmount() > 1000.0) {
            escrowDao.holdFundsInEscrow(transaction.getTransactionId(), transaction.getAmount());
        }

        orderDao.processTransaction(transaction, escrowOk);
        transaction.setStatus("SETTLED");
        return transaction;
    }

    @Override
    public List<OrderTransaction> executeBatchSettlement(boolean simulateOutage) {
        log.info("[SERVICE] Executing batch settlement. simulateOutage={}", simulateOutage);

        if (simulateOutage) {
            throw new RuntimeException("Banking network connection lost during batch settlement");
        }

        return orderDao.findTransactions(false);
    }

    @Override
    public List<OrderTransaction> querySettledTransactions() {
        log.info("[SERVICE] Querying settled transactions via DAO");
        return orderDao.findTransactions(false);
    }
}
