# Week 06: Tiered Access REST Security Shield (HTTP Basic, BCrypt, RBAC Filter Chain)

## Overview
This laboratory implements an enterprise perimeter defense shield protecting the Corporate Employee REST API using Spring Security 6/7. It configures a stateless `SecurityFilterChain` bean utilizing HTTP Basic authentication, disables CSRF for REST clients, establishes granular Role-Based Access Control (RBAC) across HTTP verbs, hashes credentials with BCrypt, and authenticates against custom relational database tables (`members` and `roles`) using `JdbcUserDetailsManager`.

---

## Key Technical Objectives
1. **Spring Security Filter Chain Architecture:**
   - Stateless request authorization intercepting requests via servlet filters (`BasicAuthenticationFilter`, `AuthorizationFilter`).
2. **Granular RBAC Route Authorization:**
   - `ROLE_EMPLOYEE`: Allowed `GET /api/employees` and `GET /api/employees/**`.
   - `ROLE_MANAGER`: Allowed `POST /api/employees`, `PUT /api/employees`, and `PATCH /api/employees/**`.
   - `ROLE_ADMIN`: Exclusively authorized to execute `DELETE /api/employees/**`.
3. **Database Authentication (`JdbcUserDetailsManager`):**
   - Queries custom database tables:
     - Users: `"select user_id, pw, active from members where user_id=?"`
     - Authorities: `"select user_id, role from roles where user_id=?"`
4. **Salted BCrypt Encryption:**
   - Verifies passwords matching stored one-way salted BCrypt hashes (`{bcrypt}$2a$10$...`).
5. **Automated MockMvc Security Test Suite:**
   - 12 comprehensive tests validating unauthenticated rejections (401), invalid credentials, RBAC restrictions across roles (403 Forbidden), and authorized mutations (200 OK).

---

## Security Credentials & Roles Matrix
| Username | Password (Plaintext) | Password (Database Stored) | Assigned Roles | Permitted Actions |
| :--- | :--- | :--- | :--- | :--- |
| `john` | `fun123` | `{bcrypt}$2a$10$...` | `ROLE_EMPLOYEE` | Read-only (`GET`) |
| `mary` | `fun123` | `{bcrypt}$2a$10$...` | `ROLE_EMPLOYEE`, `ROLE_MANAGER` | Read, Create & Update (`GET`, `POST`, `PUT`, `PATCH`) |
| `susan` | `fun123` | `{bcrypt}$2a$10$...` | `ROLE_EMPLOYEE`, `ROLE_MANAGER`, `ROLE_ADMIN` | Full Access including Delete (`DELETE`) |

---

## Verification & Test Output
Run the complete automated test suite:
```bash
mvn clean test
```
Result:
```text
[INFO] Tests run: 12, Failures: 0, Errors: 0, Skipped: 0
[INFO] BUILD SUCCESS
```
