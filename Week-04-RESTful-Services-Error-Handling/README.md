# Week 04: Corporate Enterprise Employee REST Gateway

A production-grade Spring Boot 4.1.1 laboratory demonstrating enterprise 3-Tier RESTful architecture (Controller $\to$ Service $\to$ DAO/Repository), complete HTTP CRUD verbs (`GET`, `POST`, `PUT`, `PATCH`, `DELETE`), and centralized global exception handling via `@ControllerAdvice`.

---

## Technical Objectives Covered
- **3-Tier Enterprise Architecture**:
  - **Presentation Layer**: `EmployeeRestController` managing REST endpoints under `/api/employees`.
  - **Service Layer**: `EmployeeServiceImpl` annotated with `@Service` and `@Transactional`.
  - **Data Access Tier**: `EmployeeDAOJpaImpl` annotated with `@Repository` and using `EntityManager`.
- **HTTP REST Semantics**:
  - `GET /api/employees`: Resource collection retrieval.
  - `GET /api/employees/{id}`: Single resource retrieval with 404 error mapping.
  - `POST /api/employees`: Idempotent creation forcing ID reset (`id=0`).
  - `PUT /api/employees`: Full entity replacement/update.
  - `PATCH /api/employees/{id}`: Partial field updates with ID immutability checks.
  - `DELETE /api/employees/{id}`: Resource removal returning status envelope.
- **Global Error Handling Pipeline**:
  - `EmployeeNotFoundException`: Custom domain exception.
  - `EmployeeErrorResponse`: Standardized JSON envelope containing `status`, `message`, and `timeStamp`.
  - `EmployeeRestExceptionHandler`: Global `@ControllerAdvice` interceptor returning HTTP 404 for missing entities and HTTP 400 for generic bad requests.
- **Dual Profile Strategy**: Production profile connected to local MySQL `employee_directory`, with an in-memory H2 database profile (`application-test.properties`) for deterministic test suites.

---

## Architectural Endpoints

| HTTP Method | Endpoint | Description | Expected Status |
| :--- | :--- | :--- | :--- |
| `GET` | `/api/employees` | List all employees | `200 OK` |
| `GET` | `/api/employees/{id}` | Lookup employee by ID | `200 OK` / `404 Not Found` |
| `POST` | `/api/employees` | Create and persist employee | `200 OK` |
| `PUT` | `/api/employees` | Update existing employee | `200 OK` |
| `PATCH` | `/api/employees/{id}` | Partial field update | `200 OK` / `404 Not Found` |
| `DELETE` | `/api/employees/{id}` | Delete employee by ID | `200 OK` / `404 Not Found` |

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

### 3. Verify Error Handling Pipeline
```bash
# Query non-existent ID (Returns 404 JSON envelope)
curl -s -i http://localhost:8080/api/employees/9999
```
Expected output:
```json
{
  "status": 404,
  "message": "Employee id not found - 9999",
  "timeStamp": 1728410000000
}
```
