# Week 01 Lab: Enterprise Service Metadata & Health Gateway

## Overview
This laboratory is the first practical milestone in the 10-week Spring ecosystem curriculum. It covers the core mechanics of Spring Boot 4.1.1:
* Starter POM dependencies (`spring-boot-starter-web`, `spring-boot-starter-actuator`, `spring-boot-devtools`).
* Component architecture and composite bootstrapping via `@SpringBootApplication`.
* Dynamic configuration and externalized properties injection using `@Value`.
* Operational production observability and customized telemetry using Spring Boot Actuator (`/actuator/health`, `/actuator/info`).

## Technical Specifications
* **Spring Boot Version**: 4.1.1
* **Java Version**: 21 LTS
* **Embedded Server**: Tomcat 11
* **Packaging**: JAR

## Module Components
* `com.spring.lab.overview.OverviewApplication`: Application bootstrap entry point.
* `com.spring.lab.overview.controller.SystemInfoController`: REST controller exposing `GET /` and `GET /api/v1/system-info` with injected properties (`coach.name`, `team.name`, `app.environment`, `cluster.node.id`, `cluster.region`).
* `com.spring.lab.overview.actuator.CustomClusterHealthIndicator`: Custom Actuator `HealthIndicator` exposing live cluster node state, heap memory statistics, and operational health.
* `com.spring.lab.overview.OverviewApplicationTests`: Full MockMvc integration test suite asserting API responses and Actuator telemetry.

## How to Run & Verify

### 1. Build and Run Tests
```bash
mvn clean test
```

### 2. Launch Application
```bash
mvn spring-boot:run
```

### 3. Verify Endpoints
* **Root Endpoint**:
  ```bash
  curl http://localhost:8080/
  ```
* **System Metadata**:
  ```bash
  curl http://localhost:8080/api/v1/system-info
  ```
* **Actuator Health**:
  ```bash
  curl http://localhost:8080/actuator/health
  ```
* **Actuator Info**:
  ```bash
  curl http://localhost:8080/actuator/info
  ```
