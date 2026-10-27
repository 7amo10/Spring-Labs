# Week 08: Secure Enterprise Employee Administration Portal

Full-stack Spring Boot web application integrating **Spring MVC**, **Spring Data JPA**, **Thymeleaf**, and **Spring Security 6** with custom form-based authentication, CSRF defense, and Role-Based Access Control (RBAC).

## Architecture Overview

- **Spring MVC Front Controller:** Requests are routed through the `DispatcherServlet` to `StaffAdministrationController`, binding data models and resolving Thymeleaf server-side templates.
- **Post-Redirect-Get (PRG) Pattern:** Form mutation operations (`POST /staff/save`, `POST /staff/delete`) return HTTP 302 redirects (`redirect:/staff/list`) to completely eliminate duplicate form submission and browser refresh warnings.
- **Form-Based Authentication:** Custom Bootstrap-styled login view (`/login`), processing URL (`/authenticate`), error notification (`/login?error`), and secure logout (`/logout`) returning to `/login?logout`.
- **Relational Database Authentication:** `JdbcUserDetailsManager` configured with custom SQL queries targeting `members` and `roles` relational tables with salted **BCrypt** password hashing.
- **Role-Based Access Control (RBAC):**
  - `ROLE_EMPLOYEE`: Read-only access to staff directory listing (`/staff/list`).
  - `ROLE_MANAGER`: Full read, creation, and modification permissions (`/staff/addForm`, `/staff/editForm`, `/staff/save`).
  - `ROLE_ADMIN`: Complete administrative privileges, including deletion rights (`/staff/delete`).
- **Thymeleaf Spring Security Dialect:** Dynamically renders UI action buttons (`sec:authorize="hasAnyRole('MANAGER', 'ADMIN')"`, `sec:authorize="hasRole('ADMIN')"`) and displays user identity and role badges (`sec:authentication="name"`, `sec:authentication="principal.authorities"`).
- **Authentication Event Auditing:** `AuthenticationEventListener` capturing and logging `AuthenticationSuccessEvent` and `AbstractAuthenticationFailureEvent` for intrusion detection.
- **Cross-Site Request Forgery (CSRF) Defense:** Synchronizer token pattern automatically included in Thymeleaf forms (`th:action`), protecting all mutating HTTP operations.

## Project Structure

```
Week-08-Secure-Employee-Administration-Portal/
├── pom.xml
├── README.md
└── src
    ├── main
    │   ├── java/com/spring/lab/admin/
    │   │   ├── AdminPortalApplication.java
    │   │   ├── config/SecurityConfig.java
    │   │   ├── controller/
    │   │   │   ├── AuthenticationController.java
    │   │   │   └── StaffAdministrationController.java
    │   │   ├── dao/EmployeeRepository.java
    │   │   ├── entity/Employee.java
    │   │   ├── event/AuthenticationEventListener.java
    │   │   └── service/
    │   │       ├── EmployeeService.java
    │   │       └── EmployeeServiceImpl.java
    │   └── resources/
    │       ├── application.properties
    │       └── templates/
    │           ├── auth/
    │           │   ├── access-denied.html
    │           │   └── login.html
    │           └── staff/
    │               ├── staff-form.html
    │               └── staff-list.html
    └── test/
        ├── java/com/spring/lab/admin/AdminPortalApplicationTests.java
        └── resources/
            ├── application.properties
            └── schema.sql
```

## Running the Application

```bash
mvn spring-boot:run
```

Access the portal at `http://localhost:8080/`.

### Default Credentials (Password: `fun123`)

| Username | Roles | Permissions |
| :--- | :--- | :--- |
| `john` | `ROLE_EMPLOYEE` | View staff roster (Read-only) |
| `mary` | `ROLE_EMPLOYEE`, `ROLE_MANAGER` | View, add, and edit staff |
| `susan` | `ROLE_EMPLOYEE`, `ROLE_MANAGER`, `ROLE_ADMIN` | Full permissions including delete |

## Running Automated Tests

```bash
mvn clean test
```
