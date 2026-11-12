package com.spring.lab.aop;

import com.spring.lab.aop.aspect.AuditRegistry;
import com.spring.lab.aop.dao.OrderDao;
import com.spring.lab.aop.entity.OrderTransaction;
import com.spring.lab.aop.service.PaymentSettlementService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Profile;

import java.util.List;

@SpringBootApplication
public class AopAuditApplication {

    private static final Logger log = LoggerFactory.getLogger(AopAuditApplication.class);

    public static void main(String[] args) {
        SpringApplication.run(AopAuditApplication.class, args);
    }

    @Bean
    @Profile("!test")
    public CommandLineRunner commandLineRunner(OrderDao orderDao,
                                               PaymentSettlementService paymentSettlementService,
                                               AuditRegistry auditRegistry) {
        return args -> {
            log.info("=================================================================");
            log.info("STARTING ENTERPRISE AOP SECURITY AUDIT & PROFILING DEMONSTRATION");
            log.info("=================================================================");

            // 1. Demonstrate @Before advice & JoinPoint parameter extraction
            log.info("\n--- STEP 1: Demonstrating @Before Advice & JoinPoint Parameter Extraction ---");
            OrderTransaction order1 = new OrderTransaction("TXN-7001", "ACC-9988-7766", 4500.0, "usd", "CONFIDENTIAL");
            orderDao.processTransaction(order1, true);

            // 2. Demonstrate Pointcut Noise Filtering (getters/setters bypassed)
            log.info("\n--- STEP 2: Demonstrating Pointcut Noise Filtering (Getters/Setters) ---");
            orderDao.setClusterId("CLUSTER-NORTH-02");
            String activeCluster = orderDao.getClusterId();
            log.info("Directly invoked getter/setter without AOP interception: cluster={}", activeCluster);

            // 3. Demonstrate @Around Advice & SLA Threshold Alerting
            log.info("\n--- STEP 3: Demonstrating @Around Advice with SLA Violation Alerting ---");
            OrderTransaction delayedOrder = new OrderTransaction("TXN-7002", "ACC-1234-5678", 12500.0, "eur", "RESTRICTED");
            paymentSettlementService.settleOrder(delayedOrder, true);

            // 4. Demonstrate @AfterReturning Advice (Data Sanitization & Post-Processing)
            log.info("\n--- STEP 4: Demonstrating @AfterReturning Advice (Data Sanitization) ---");
            List<OrderTransaction> transactions = orderDao.findTransactions(false);
            for (OrderTransaction t : transactions) {
                log.info("Sanitized Output: id={}, maskedAccount={}, currency={}",
                        t.getTransactionId(), t.getAccountNumber(), t.getCurrency());
            }

            // 5. Demonstrate @AfterThrowing & @After (Finally) Advice
            log.info("\n--- STEP 5: Demonstrating @AfterThrowing & @After Finally Advice ---");
            try {
                paymentSettlementService.executeBatchSettlement(true);
            } catch (Exception ex) {
                log.info("Application caught simulated exception: {}", ex.getMessage());
            }

            log.info("=================================================================");
            log.info("AOP DEMONSTRATION COMPLETE. TOTAL AUDIT ENTRIES RECORDED: {}", auditRegistry.getAuditEntries().size());
            log.info("SLA ALERTS TRIGGERED: {}", auditRegistry.getSlaAlerts().size());
            log.info("=================================================================");
        };
    }
}
