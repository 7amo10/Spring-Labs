# Week 05: Spring Data JPA Automated Repositories & Spring Data REST Hypermedia Engine

## Overview
This laboratory implements the complete Spring Data persistence layer and automated hypermedia REST engine for the Corporate Employee directory. It eliminates all manual DAO/Repository boilerplate code by inheriting from Spring Data JPA's `JpaRepository<Employee, Integer>`, and automatically exposes HATEOAS-compliant hypermedia REST endpoints via `spring-boot-starter-data-rest`.

---

## Key Technical Objectives
1. **Zero-Boilerplate Persistence (`JpaRepository`):**
   - Direct interface extension of `JpaRepository<Employee, Integer>` generating runtime dynamic bytecode proxies backed by `SimpleJpaRepository` and JPA `EntityManager`.
2. **Derived Query Methods with Type-Safe JPQL Parsing:**
   - Declarative method signatures (`findByLastName`, `findByEmailContaining`) parsed by the Spring container into parameterized, SQL-injection-resistant JPQL queries with native `Pageable` and pagination slicing.
3. **Automated Hypermedia REST APIs (Spring Data REST):**
   - Exposes full CRUD REST endpoints out-of-the-box with standard HAL JSON hypermedia representations (`_embedded`, `_links`, navigation hrefs).
4. **Custom REST Resource Configuration:**
   - Overriding default entity plurals via `@RepositoryRestResource(path = "members", collectionResourceRel = "members")`.
   - Global API prefixing via `spring.data.rest.base-path=/api`.
5. **Pagination & Multi-Property Sorting:**
   - Configurable pagination (`?page=0&size=20`) and URL sorting (`?sort=lastName,desc`).
6. **Automated MockMvc Integration Test Suite:**
   - 11 comprehensive automated tests covering derived queries, HAL payloads, pagination metadata, sorting, search methods, and CRUD verbs.

---

## Project Structure
```text
Week-05-Spring-Data-JPA-and-REST/
├── pom.xml
├── README.md
└── src
    ├── main
    │   ├── java/com/spring/lab/repository
    │   │   ├── RepositoryApplication.java
    │   │   ├── dao/EmployeeRepository.java
    │   │   └── entity/Employee.java
    │   └── resources
    │       └── application.properties
    └── test
        ├── java/com/spring/lab/repository
        │   └── RepositoryApplicationTests.java
        └── resources
            └── application.properties
```

---

## API Endpoints Exposed
| Method | Path | Description |
| :--- | :--- | :--- |
| `GET` | `/api/members` | Retrieve paginated collection of employees in HAL JSON |
| `GET` | `/api/members?sort=lastName,desc` | Retrieve collection sorted by last name descending |
| `GET` | `/api/members?page=0&size=2` | Retrieve first page of 2 items with navigation links |
| `GET` | `/api/members/{id}` | Retrieve single employee resource |
| `POST` | `/api/members` | Create new employee resource |
| `PUT` | `/api/members/{id}` | Replace existing employee resource |
| `PATCH` | `/api/members/{id}` | Partially update employee resource |
| `DELETE` | `/api/members/{id}` | Delete employee resource |
| `GET` | `/api/members/search` | Discover available derived query endpoints |
| `GET` | `/api/members/search/by-last-name?lastName={name}` | Search by exact last name |
| `GET` | `/api/members/search/by-email-containing?keyword={kw}` | Search by email substring |

---

## Verification & Testing
Run the complete automated test suite:
```bash
mvn clean test
```
Result:
```text
[INFO] Tests run: 11, Failures: 0, Errors: 0, Skipped: 0
[INFO] BUILD SUCCESS
```
