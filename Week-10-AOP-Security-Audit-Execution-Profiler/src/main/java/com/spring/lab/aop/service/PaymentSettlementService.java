package com.spring.lab.aop.service;

import com.spring.lab.aop.entity.OrderTransaction;
import java.util.List;

public interface PaymentSettlementService {

    OrderTransaction settleOrder(OrderTransaction transaction, boolean simulateDelay);

    List<OrderTransaction> executeBatchSettlement(boolean simulateOutage);

    List<OrderTransaction> querySettledTransactions();
}
