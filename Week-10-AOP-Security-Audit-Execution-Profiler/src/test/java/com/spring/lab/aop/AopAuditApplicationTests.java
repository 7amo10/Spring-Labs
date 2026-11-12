package com.spring.lab.aop;

import com.spring.lab.aop.aspect.AuditRegistry;
import com.spring.lab.aop.dao.OrderDao;
import com.spring.lab.aop.entity.OrderTransaction;
import com.spring.lab.aop.service.PaymentSettlementService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class AopAuditApplicationTests {

    @Autowired
    private OrderDao orderDao;

    @Autowired
    private PaymentSettlementService paymentSettlementService;

    @Autowired
    private AuditRegistry auditRegistry;

    @Autowired
    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        auditRegistry.clear();
    }

    @Test
    @DisplayName("Verify @Before advice intercepts DAO and extracts arguments via JoinPoint")
    void testSecurityAuditAspect_InterceptsDao() {
        OrderTransaction txn = new OrderTransaction("TXN-101", "ACC-1234-5678", 2500.0, "usd", "RESTRICTED");

        orderDao.processTransaction(txn, true);

        assertThat(auditRegistry.getAuditEntries())
                .anyMatch(entry -> entry.getAspectName().equals("SecurityAuditAspect") &&
                        entry.getAdviceType().equals("@Before") &&
                        entry.getPayloadDetails().contains("TXN-101"));
    }

    @Test
    @DisplayName("Verify pointcut noise filtering excludes DAO getter and setter methods")
    void testPointcutExcludesGettersAndSetters() {
        orderDao.setClusterId("CLUSTER-EU-WEST");
        String cluster = orderDao.getClusterId();

        assertThat(cluster).isEqualTo("CLUSTER-EU-WEST");
        assertThat(auditRegistry.getAuditEntries())
                .noneMatch(entry -> entry.getTargetMethod().contains("getClusterId") ||
                        entry.getTargetMethod().contains("setClusterId"));
    }

    @Test
    @DisplayName("Verify @Around advice measures execution duration and flags SLA alerts on delay")
    void testPerformanceProfilerAspect_MeasuresDurationAndFlagsSla() {
        OrderTransaction txn = new OrderTransaction("TXN-202", "ACC-8877-6655", 8000.0, "eur", "CONFIDENTIAL");

        OrderTransaction settled = paymentSettlementService.settleOrder(txn, true);

        assertThat(settled.getStatus()).isEqualTo("SETTLED");
        assertThat(auditRegistry.getAuditEntries())
                .anyMatch(entry -> entry.getAspectName().equals("PerformanceProfilerAspect") &&
                        entry.getAdviceType().equals("@Around"));
        assertThat(auditRegistry.getSlaAlerts())
                .anyMatch(alert -> alert.contains("SLA BREACH ALERT"));
    }

    @Test
    @DisplayName("Verify @AfterReturning advice sanitizes sensitive account numbers and normalizes currencies")
    void testDataSanitizationAspect_PostProcessesResults() {
        List<OrderTransaction> txns = orderDao.findTransactions(false);

        assertThat(txns).isNotEmpty();
        for (OrderTransaction item : txns) {
            assertThat(item.getAccountNumber()).contains("****");
            assertThat(item.getCurrency()).isEqualTo(item.getCurrency().toUpperCase());
        }

        assertThat(auditRegistry.getAuditEntries())
                .anyMatch(entry -> entry.getAspectName().equals("DataSanitizationAspect") &&
                        entry.getAdviceType().equals("@AfterReturning"));
    }

    @Test
    @DisplayName("Verify @AfterThrowing captures exceptions and @After executes finally cleanup")
    void testExceptionLoggerAspect_CapturesFailuresAndRunsFinally() {
        assertThatThrownBy(() -> paymentSettlementService.executeBatchSettlement(true))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Banking network connection lost");

        assertThat(auditRegistry.getAuditEntries())
                .anyMatch(entry -> entry.getAspectName().equals("ExceptionLoggerAspect") &&
                        entry.getAdviceType().equals("@AfterThrowing"));

        assertThat(auditRegistry.getAuditEntries())
                .anyMatch(entry -> entry.getAspectName().equals("ExceptionLoggerAspect") &&
                        entry.getAdviceType().equals("@After"));
    }

    @Test
    @DisplayName("Verify Spring Boot 4 URL path segment API versioning v1 and v2 GET endpoints")
    void testApiVersioningEndpoints() throws Exception {
        mockMvc.perform(get("/api/1/orders"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.apiVersion").value("v1.0-legacy"))
                .andExpect(jsonPath("$.settledOrdersCount").isNumber());

        mockMvc.perform(get("/api/2/orders"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.apiVersion").value("v2.0-modern"))
                .andExpect(jsonPath("$.settledTransactions").isArray());
    }

    @Test
    @DisplayName("Verify Spring Boot 4 URL path segment API versioning v2 POST endpoint")
    void testApiVersioningPostOrderV2() throws Exception {
        String jsonPayload = """
                {
                    "transactionId": "TXN-9090",
                    "accountNumber": "ACC-9988-7766",
                    "amount": 3400.0,
                    "currency": "usd",
                    "securityClassification": "CONFIDENTIAL"
                }
                """;

        mockMvc.perform(post("/api/2/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonPayload))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.transactionId").value("TXN-9090"))
                .andExpect(jsonPath("$.status").value("SETTLED"));

        assertThat(auditRegistry.getAuditEntries())
                .anyMatch(entry -> entry.getTargetMethod().contains("submitOrderV2"));
    }

    @Test
    @DisplayName("Verify Actuator Health endpoint is accessible")
    void testActuatorHealthEndpoint() throws Exception {
        mockMvc.perform(get("/actuator/health"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("UP"));
    }
}
