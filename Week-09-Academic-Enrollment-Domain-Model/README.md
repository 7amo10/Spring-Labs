# Week 09 Lab: University Academic & Course Enrollment Domain Model

## Overview
This laboratory module implements an enterprise academic domain model demonstrating **Advanced JPA & Hibernate Relational Topologies and Fetch Optimization** using Spring Boot 4 and Jakarta Persistence. The application models faculty profiles, academic course listings, student reviews, and multi-disciplinary student course enrollments.

---

## Architectural Topologies Implemented

```
                     +--------------------+
                     |  InstructorDetail  |
                     +--------------------+
                               ^ (1:1 Bidirectional)
                               |
                        +--------------+
                        |  Instructor  |
                        +--------------+
                               |
                               v (1:N Bidirectional)
                         +------------+
                         |   Course   |
                         +------------+
                        /              \
  (1:N Unidirectional) /                \ (M:N Bidirectional)
                      v                  v
                 +--------+     +----------------+     +-----------+
                 | Review |     | course_student | <-> |  Student  |
                 +--------+     +----------------+     +-----------+
```

1. **One-to-One (1:1) Bidirectional Association (`Instructor` $\leftrightarrow$ `InstructorDetail`):**
   * Foreign key `instructor_detail_id` maintained on table `instructor`.
   * Mapped bidirectionally using `mappedBy = "instructorDetail"`.
   * Fine-tuned cascading: `Instructor` cascades `CascadeType.ALL` to detail; `InstructorDetail` deliberately omits `CascadeType.REMOVE` to safeguard the instructor when details are removed.
2. **One-to-Many / Many-to-One (1:N) Bidirectional Association (`Instructor` $\leftrightarrow$ `Course`):**
   * Foreign key `instructor_id` maintained on table `course`.
   * `Instructor` maps courses with `fetch = FetchType.LAZY` and selective cascades (`PERSIST`, `MERGE`, `DETACH`, `REFRESH`).
   * Explicit entity dissociation logic breaks course foreign keys before instructor deletion, preserving course curriculum records.
3. **One-to-Many (1:N) Unidirectional Association (`Course` $\rightarrow$ `Review`):**
   * Foreign key `course_id` maintained on table `review` via `@JoinColumn(name = "course_id")` without a back-reference.
   * `CascadeType.ALL` ensures review lifecycles are completely managed by parent courses.
4. **Many-to-Many (M:N) Bidirectional Association (`Course` $\leftrightarrow$ `Student`):**
   * Managed through dedicated relational join table `course_student`.
   * Symmetrical convenience methods (`course.addStudent()`, `student.addCourse()`).
   * Cascade types omit `REMOVE` so deleting a course preserves students, and deleting a student preserves courses while cleaning up join table records.
5. **Fetch Optimization & N+1 Problem Prevention:**
   * Selective JPQL `JOIN FETCH` queries eliminate query proliferation by loading associated entity graphs in a single SQL execution.

---

## Component Architecture

* **Entities (`com.spring.lab.academic.entity`):**
  * `Instructor`: Faculty entity with identity mapping and relationship helpers.
  * `InstructorDetail`: Portfolio and specialization details.
  * `Course`: Hub entity linking instructors, reviews, and enrolled students.
  * `Review`: Student rating and evaluation comments.
  * `Student`: Enrolled learner profile.
* **DAO Layer (`com.spring.lab.academic.dao`):**
  * `AcademicDao`: Interface defining CRUD, entity dissociation, and fetch-optimized queries.
  * `AcademicDaoImpl`: Production implementation leveraging Jakarta Persistence `EntityManager` and typed JPQL queries.
* **Application Runner (`com.spring.lab.academic`):**
  * `AcademicApplication`: Main Spring Boot application and console lifecycle demonstrator (`@Profile("!test")`).

---

## Running the Automated Tests

Execute the comprehensive JUnit 5 integration test suite:

```bash
mvn clean test
```

All 11 integration test scenarios will execute against an in-memory H2 database, validating association lifecycles, cascading rules, entity dissociation, and `JOIN FETCH` query efficiency.
