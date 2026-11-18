# Week 11 Lab: Enterprise Banking Security Perimeter

## Architectural Overview
This module implements the **Week 11 Practical Engineering Lab** for the Spring Security Master Curriculum, establishing an enterprise-grade banking security perimeter based on Madan Reddy's Eazy Bytes curriculum (Sections 01 through 08).

### Covered Sections & Core Security Topologies:
1. **Section 1: Servlet Interception Architecture**  
   Dissects the request dispatch lifecycle across `DelegatingFilterProxy`, `FilterChainProxy`, and component-based `SecurityFilterChain` beans.
2. **Section 2: Custom Security Filter Chain Configurations**  
   Implements explicit route-matching policies with `requestMatchers()`, segmenting public informational routes (`/api/v1/notices`, `/api/v1/support/inquiries`, `/api/v1/auth/register`) from strictly fortified financial resources.
3. **Section 3: User Details Management**  
   Models user identity with JPA entity persistence (`BankUser`, `BankAuthority`) and provides a custom `BankUserDetailsService` translating database users into Spring Security `UserDetails`.
4. **Section 4: Password Management with PasswordEncoders**  
   Adopts `PasswordEncoderFactories.createDelegatingPasswordEncoder()` (prefixing hashes with `{bcrypt}`) and implements Spring Security 6.3+ `CompromisedPasswordChecker` to detect and reject breached dictionary passwords at registration.
5. **Section 5: Custom Authentication Provider**  
   Implements `BankUsernamePasswordAuthenticationProvider` to evaluate raw credentials and enforce custom domain-level security rules (rejecting deactivated accounts with `DisabledException`).
6. **Section 6: Custom Exception Handling & Authentication Events**  
   Implements `BankBasicAuthenticationEntryPoint` (401 Unauthorized) and `BankAccessDeniedHandler` (403 Forbidden) emitting standardized JSON error payloads, accompanied by an event listener tracking success and failure telemetry.
7. **Section 7: Understanding CORS & CSRF**  
   Configures origin whitelisting (`CorsConfigurationSource`) for SPA clients (`http://localhost:4200`) and enforces Cookie-based CSRF protection using `CookieCsrfTokenRepository.withHttpOnlyFalse()` paired with `CsrfTokenRequestAttributeHandler` and a custom `CsrfCookieFilter`.
8. **Section 8: Fine-Grained Authorization (Roles vs. Authorities)**  
   Enforces authority-based route protection (`hasAuthority('VIEW_ACCOUNT')`, `hasAuthority('VIEW_BALANCE')`, `hasAuthority('VIEW_CARDS')`, `hasAuthority('VIEW_LOANS')`) alongside administrative role gating (`hasRole('ADMIN')`).

---

## API Endpoint Matrix

| HTTP Method | Route | Security Policy / Required Authority | Purpose |
| :--- | :--- | :--- | :--- |
| `GET` | `/api/v1/auth/health` | `permitAll()` | Liveness probe & filter chain verification |
| `POST` | `/api/v1/auth/register` | `permitAll()` | Customer registration & password breach check |
| `GET` | `/api/v1/notices` | `permitAll()` | Active public announcements with Cache-Control |
| `POST` | `/api/v1/support/inquiries` | `permitAll()` | Public customer support ticketing |
| `GET` | `/api/v1/users/profile` | `authenticated()` | Authenticated customer profile details |
| `GET` | `/api/v1/accounts/my-account` | `hasAnyAuthority('VIEW_ACCOUNT', 'ROLE_ADMIN')` | Customer bank account details |
| `GET` | `/api/v1/accounts/user` | `hasAnyAuthority('VIEW_ACCOUNT', 'ROLE_ADMIN')` | Multi-account listing by owner email |
| `GET` | `/api/v1/transactions/my-balance` | `hasAnyAuthority('VIEW_BALANCE', 'ROLE_ADMIN')` | Historical transactions & ledger balances |
| `GET` | `/api/v1/cards/my-cards` | `hasAnyAuthority('VIEW_CARDS', 'ROLE_ADMIN')` | Credit and debit card portfolio |
| `GET` | `/api/v1/loans/my-loans` | `hasAnyAuthority('VIEW_LOANS', 'ROLE_ADMIN')` | Active loan installments & outstanding balances |
| `GET` | `/api/v1/admin/audit` | `hasRole('ADMIN')` | Administrative security audit statistics |

---

## Test Verification
Run the automated test suite with Maven:
```bash
mvn clean test
```
All integration test scenarios verify end-to-end security configurations using Spring MVC `MockMvc` and `spring-security-test`.
