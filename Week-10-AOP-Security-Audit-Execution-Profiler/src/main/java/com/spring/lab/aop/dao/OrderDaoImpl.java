package com.spring.lab.aop.dao;

import com.spring.lab.aop.entity.OrderTransaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

@Repository
public class OrderDaoImpl implements OrderDao {

    private static final Logger log = LoggerFactory.getLogger(OrderDaoImpl.class);

    private String clusterId = "PRIMARY-CLUSTER-EAST";
    private String tenantCode = "TENANT-CORP-01";

    private final List<OrderTransaction> storage = new ArrayList<>();

    @Override
    public void processTransaction(OrderTransaction transaction, boolean isEscrowRequired) {
        log.info("[DAO] Processing transaction: id={}, amount={}, escrow={}",
                transaction.getTransactionId(), transaction.getAmount(), isEscrowRequired);
        storage.add(transaction);
    }

    @Override
    public List<OrderTransaction> findTransactions(boolean simulateOutage) {
        log.info("[DAO] Finding order transactions. simulateOutage={}", simulateOutage);

        if (simulateOutage) {
            throw new IllegalStateException("Database cluster outage detected during transaction lookup");
        }

        // Return copy of stored or sample transactions
        List<OrderTransaction> results = new ArrayList<>();
        results.add(new OrderTransaction("TXN-301", "ACC-1111-2222", 1500.0, "usd", "CONFIDENTIAL", "SETTLED"));
        results.add(new OrderTransaction("TXN-302", "ACC-3333-4444", 2750.5, "eur", "RESTRICTED", "SETTLED"));
        results.add(new OrderTransaction("TXN-303", "ACC-5555-6666", 890.0, "gbp", "PUBLIC", "SETTLED"));
        return results;
    }

    @Override
    public int archiveOldTransactions(int retentionDays) {
        log.info("[DAO] Archiving transactions older than {} days", retentionDays);
        return 42;
    }

    @Override
    public String getClusterId() {
        log.info("[DAO] in getClusterId()");
        return clusterId;
    }

    @Override
    public void setClusterId(String clusterId) {
        log.info("[DAO] in setClusterId() with value: {}", clusterId);
        this.clusterId = clusterId;
    }

    @Override
    public String getTenantCode() {
        log.info("[DAO] in getTenantCode()");
        return tenantCode;
    }

    @Override
    public void setTenantCode(String tenantCode) {
        log.info("[DAO] in setTenantCode() with value: {}", tenantCode);
        this.tenantCode = tenantCode;
    }
}
