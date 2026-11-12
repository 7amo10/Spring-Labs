# Week 10 Lab: Automated Cross-Layer Security Audit & Execution Profiler

## Overview
This laboratory module implements an enterprise-grade cross-cutting auditing and performance profiling framework using **Spring AOP, AspectJ Weaver, and Spring Boot 4 / Spring Framework 7**. It decouples non-functional concerns (security auditing, execution profiling, data sanitization, and exception logging) from business services, data access objects, and REST controllers.

---

## Architectural Pipeline

```
[ Client Request ]
       |
       v
+-----------------------------------------------------------------------------------+
| REST Controller Tier (/api/{version}/orders)                                      |
| - URL Path Segment API Versioning: v1.0 (Legacy) & v2.0 (Modern)                  |
+-----------------------------------------------------------------------------------+
       |
       v
+-----------------------------------------------------------------------------------+
| AOP Interception Layer (Strict Precedence via @Order)                             |
|                                                                                   |
|  [@Order(1) SecurityAuditAspect]                                                  |
|     * @Before advice reading JoinPoint & MethodSignature                          |
|     * Inspects arguments, payload security classification, logs to AuditRegistry  |
|                                                                                   |
|  [@Order(2) PerformanceProfilerAspect]                                            |
|     * @Around advice wrapping ProceedingJoinPoint                                 |
|     * High-precision millisecond timing; triggers SLA BREACH ALERT if > 100ms     |
|     * Re-throws exceptions without swallowed errors                               |
|                                                                                   |
|  [@Order(3) DataSanitizationAspect]                                               |
|     * @AfterReturning post-processing returned collections                        |
|     * Masks confidential account numbers (ACC-****-4321) & uppercases currencies  |
|                                                                                   |
|  [@Order(4) ExceptionLoggerAspect]                                                |
|     * @AfterThrowing logging error details on unhandled failures                  |
|     * @After finally advice performing context cleanup regardless of outcome      |
+-----------------------------------------------------------------------------------+
       |
       v
+-----------------------------------------------------------------------------------+
| Business Service Tier (PaymentSettlementService)                                  |
+-----------------------------------------------------------------------------------+
       |
       v
+-----------------------------------------------------------------------------------+
| Data Access Tier (OrderDao, EscrowDao)                                            |
| * Composite Pointcuts: forDaoPackage() && !(getter() || setter())                 |
| * Filters noisy getter/setter invocations automatically                           |
+-----------------------------------------------------------------------------------+
```

---

## Key Technical Objectives

1. **Aspect Foundations & Pointcut Declarations:**
   * Centralized pointcut catalog in `ArchitecturePointcuts` with reusable `@Pointcut` definitions matching execution across controller, service, and data access tiers.
   * Pointcut composition using Boolean operators (`&&`, `||`, `!`) to filter trivial getter and setter invocations.
2. **Aspect Ordering (`@Order`):**
   * Deterministic aspect execution sequence: Security Audit (`@Order(1)`), Performance Profiling (`@Order(2)`), Data Sanitization (`@Order(3)`), and Exception Logging (`@Order(4)`).
3. **Deep Dive into All 5 Advice Types:**
   * `@Before`: Intercepting methods, introspecting signatures and argument payloads via `JoinPoint`.
   * `@Around`: Surrounding business methods with `ProceedingJoinPoint`, calculating execution duration, enforcing SLA thresholds, and handling/rethrowing exceptions.
   * `@AfterReturning`: Intercepting method outputs, performing in-place data sanitization (masking confidential account identifiers and normalizing currency codes).
   * `@AfterThrowing`: Capturing unhandled exceptions flowing across architectural layers.
   * `@After`: Executing reliable finally cleanup logic regardless of success or failure.
4. **Spring Boot 4 / Spring Framework 7 Modern Updates:**
   * Built-in URL Path Segment API Versioning configured via `spring.mvc.apiversion.use.path-segment=1`.
   * Declarative `@GetMapping(version = "1")` and `@GetMapping(version = "2")` route dispatching in `OrderAuditRestController`.
   * Java 21 baseline and Spring Boot 4 Starter AspectJ integration (`spring-boot-starter-aspectj`).

---

## Component Architecture

* **Entities & Audit Models (`com.spring.lab.aop.entity`):**
  * `OrderTransaction`: Core financial domain object with security classifications and statuses.
  * `AuditLogEntry`: Structured audit event record with timestamps, advice types, and target methods.
* **Aspects & Pointcuts (`com.spring.lab.aop.aspect`):**
  * `ArchitecturePointcuts`: Central pointcut repository with noise filtering.
  * `AuditRegistry`: Thread-safe singleton capturing audit trails and SLA alerts for empirical verification.
  * `SecurityAuditAspect` (`@Order(1)`): `@Before` inspection aspect.
  * `PerformanceProfilerAspect` (`@Order(2)`): `@Around` profiling and SLA monitoring aspect.
  * `DataSanitizationAspect` (`@Order(3)`): `@AfterReturning` masking aspect.
  * `ExceptionLoggerAspect` (`@Order(4)`): `@AfterThrowing` and `@After` finally aspect.
* **Data Access Layer (`com.spring.lab.aop.dao`):**
  * `OrderDao` / `OrderDaoImpl`: Transaction storage and lifecycle methods.
  * `EscrowDao` / `EscrowDaoImpl`: Escrow verification and fund holding.
* **Service Layer (`com.spring.lab.aop.service`):**
  * `PaymentSettlementService` / `PaymentSettlementServiceImpl`: High-level settlement workflows with simulated delay and failure injection.
* **REST Controller Layer (`com.spring.lab.aop.rest`):**
  * `OrderAuditRestController`: Versioned API endpoints for v1 (legacy) and v2 (modern) clients.

---

## Running the Automated Tests

Execute the comprehensive JUnit 5 integration test suite:

```bash
mvn clean test
```

All 8 integration test scenarios validate aspect ordering, parameter extraction, SLA alert generation, data masking, exception logging, and versioned REST controllers.
