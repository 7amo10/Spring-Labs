# Week 07: Corporate Registration Portal & Custom Constraint Validation

Enterprise employee onboarding and benefits enrollment web application developed with **Spring MVC 6**, **Thymeleaf**, and **Jakarta Bean Validation**.

## Architecture Overview

- **Front Controller Pattern (`DispatcherServlet`):** Routes inbound web traffic through handler mappings, binding models, executing controller methods, and resolving Thymeleaf server-side HTML views.
- **Bidirectional Data Binding:** Connects HTML form input fields, dropdown select lists, radio groups, and multi-select checkboxes to an `EmployeeRegistration` form-backing object using `th:object` and `th:field`.
- **Pre-Processing via `WebDataBinder` & `StringTrimmerEditor`:** Registers a `@InitBinder` rule trimming leading/trailing whitespace and transforming blank strings to `null` to ensure `@NotNull` and `@Size` evaluate without whitespace bypasses.
- **Jakarta Bean Validation & Custom Constraints:**
  - `@NotNull` and `@Size(min=1)`: Enforces mandatory fields (`lastName`, `corporateEmail`, `salary`).
  - `@Pattern`: Enforces a 5-character alphanumeric format for office postal codes.
  - `@Min(30000)` and `@Max(250000)`: Constrains annual base compensation ranges.
  - `@CorporateEmail`: Custom constraint annotation and validator verifying that employee email addresses strictly terminate with `@company.org`.
- **Property-Driven Configuration:** Populates dynamic form choices (`corporate.departments`, `corporate.employmentTypes`, `corporate.benefits`) from `application.properties`.
- **Localized Type Mismatch Resolution:** Leverages `messages.properties` (`typeMismatch.employee.salary`) to produce intuitive error messages when non-integer strings are entered into numerical fields.

## Project Structure

```
Week-07-Corporate-Registration-Portal/
├── pom.xml
├── README.md
└── src
    ├── main
    │   ├── java/com/spring/lab/mvc/
    │   │   ├── RegistrationApplication.java
    │   │   ├── controller/EmployeeRegistrationController.java
    │   │   ├── model/EmployeeRegistration.java
    │   │   └── validation/
    │   │       ├── CorporateEmail.java
    │   │       └── CorporateEmailConstraintValidator.java
    │   └── resources/
    │       ├── application.properties
    │       ├── messages.properties
    │       ├── static/css/style.css
    │       └── templates/
    │           ├── registration-form.html
    │           └── registration-confirmation.html
    └── test/
        └── java/com/spring/lab/mvc/RegistrationApplicationTests.java
```

## Running the Application

```bash
mvn spring-boot:run
```

Access the registration portal at `http://localhost:8080/` or `http://localhost:8080/register`.

## Running Automated Tests

```bash
mvn clean test
```
