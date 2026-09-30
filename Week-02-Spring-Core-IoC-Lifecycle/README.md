# Week 02: Dynamic Athletic Dispatcher & 3rd-Party Adapter Service

A production-grade Spring Boot 4.1.1 laboratory demonstrating core Inversion of Control (IoC), Dependency Injection (DI), polymorphic qualifier resolution, bean scopes, lifecycle hooks, and Java Configuration 3rd-party bean adaptation.

---

## Technical Objectives Covered
- **Constructor vs Setter Injection**: Mandatory dependencies injected immutably via constructor; configurable/optional dependencies injected via setter methods.
- **Disambiguation with `@Qualifier` and `@Primary`**: Resolving multiple implementations of `Coach` (`CricketCoach`, `TrackCoach`, `TennisCoach`, `BaseballCoach`, `SwimCoach`).
- **Lazy Initialization (`@Lazy`)**: Deferring container bean instantiation of `TennisCoach` until the first HTTP request arrives.
- **Bean Scopes & Instance Isolation**: Comparing `@Scope("singleton")` against `@Scope("prototype")` (`BaseballCoach`) with memory identity hash verification.
- **Lifecycle Interception**: Implementing `@PostConstruct` and `@PreDestroy` callbacks, plus custom `init-method` / `destroy-method` registration.
- **3rd-Party Bean Adaptation via Java Config**: Adapting external POJO `SwimCoach` with `@Configuration` and `@Bean`.

---

## Architectural Endpoints

| HTTP Method | Endpoint | Description | Injection Mechanism |
| :--- | :--- | :--- | :--- |
| `GET` | `/api/v1/coaches/cricket` | Cricket training routine | Constructor Injection with `@Qualifier("cricketCoach")` |
| `GET` | `/api/v1/coaches/primary` | Track training routine | Implicit `@Primary` resolution without qualifier |
| `GET` | `/api/v1/coaches/tennis` | Tennis volley drills | `@Lazy` initialization triggered on first request |
| `GET` | `/api/v1/coaches/swim` | Freestyle sprint routine | Adapted 3rd-Party POJO via `@Bean` + Setter Injection |
| `GET` | `/api/v1/coaches/baseball` | Batting cage routine | `@Scope(SCOPE_PROTOTYPE)` independent instance |
| `GET` | `/api/v1/coaches/scope-check` | Compares singleton vs prototype identity | Evaluates `==` memory references & identity hashes |
| `GET` | `/api/v1/coaches/dispatch?sport=...` | Polymorphic dynamic dispatcher | Routes to requested coach dynamically |

---

## Verification & Execution

### 1. Run Automated Test Suite
```bash
mvn clean test
```

### 2. Boot Application
```bash
mvn spring-boot:run
```

### 3. Verify Scope Isolation
```bash
curl -s http://localhost:8080/api/v1/coaches/scope-check | jq .
```
Expected output:
```json
{
  "singletonVerification": {
    "scope": "SINGLETON",
    "isSameInstance": true,
    "evaluation": "PASS: Both references point to identical memory location"
  },
  "prototypeVerification": {
    "scope": "PROTOTYPE",
    "isSameInstance": false,
    "evaluation": "PASS: Distinct independent instances allocated per injection point"
  }
}
```
