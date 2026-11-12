package com.spring.lab.aop.dao;

import com.spring.lab.aop.entity.OrderTransaction;
import java.util.List;

public interface OrderDao {

    void processTransaction(OrderTransaction transaction, boolean isEscrowRequired);

    List<OrderTransaction> findTransactions(boolean simulateOutage);

    int archiveOldTransactions(int retentionDays);

    String getClusterId();

    void setClusterId(String clusterId);

    String getTenantCode();

    void setTenantCode(String tenantCode);
}
