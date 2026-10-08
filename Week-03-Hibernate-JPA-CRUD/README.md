# Week 03: Student Information System DAO Tier (Hibernate / JPA CRUD)

A production-grade Spring Boot 4.1.1 laboratory demonstrating enterprise persistence architecture using Jakarta Persistence (JPA), Hibernate ORM, `EntityManager`, `@Transactional` demarcation, and declarative JPQL queries.

---

## Technical Objectives Covered
- **Entity Declaration & Mapping**: `@Entity`, `@Table(name="student")`, `@Id`, `@GeneratedValue(strategy=GenerationType.IDENTITY)`, and column bindings.
- **DAO Architecture**: Interface-driven persistence pattern (`StudentDAO`) implemented via `StudentDAOImpl` annotated with `@Repository`.
- **EntityManager Operations**: Direct programmatic persistence management: `persist()`, `find()`, `merge()`, `remove()`, and `executeUpdate()`.
- **Transaction Management**: Wrapping state-mutating operations with Spring's `@Transactional` to guarantee ACID consistency.
- **JPQL Querying**: Declarative queries on entity types (`FROM Student WHERE lastName = :theData`) using named parameters.
- **Dual Profile Strategy**: Production profile running against local MySQL (`student_tracker`), with an isolated in-memory H2 database profile (`application-test.properties`) for deterministic test execution.

---

## Architectural Endpoints

| HTTP Method | Endpoint | Description |
| :--- | :--- | :--- |
| `GET` | `/api/v1/students` | Retrieve all students (sorted by last name) |
| `GET` | `/api/v1/students/{id}` | Lookup student by primary key ID |
| `GET` | `/api/v1/students/search/lastName?lastName=...` | JPQL filtered lookup by last name |
| `GET` | `/api/v1/students/search/email?email=...` | JPQL filtered lookup by email |
| `POST` | `/api/v1/students` | Create and persist a new student |
| `PUT` | `/api/v1/students` | Update and merge student details |
| `DELETE` | `/api/v1/students/{id}` | Remove student by primary key |
| `DELETE` | `/api/v1/students` | Bulk deletion of all student records |

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

### 3. Verify HTTP Operations
```bash
# Create student
curl -X POST http://localhost:8080/api/v1/students \
  -H "Content-Type: application/json" \
  -d '{"firstName":"Daffy","lastName":"Duck","email":"daffy@luv2code.com"}'

# Query all students
curl -s http://localhost:8080/api/v1/students | jq .
```
